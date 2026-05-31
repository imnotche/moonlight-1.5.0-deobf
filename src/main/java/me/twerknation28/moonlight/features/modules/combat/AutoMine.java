package me.twerknation28.moonlight.features.modules.combat;

import org.jetbrains.annotations.NotNull;
import me.twerknation28.moonlight.util.Util;
import net.minecraft.block.BlockState;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import me.twerknation28.moonlight.manager.RotationManager;
import me.twerknation28.moonlight.manager.NetworkManager;
import java.util.List;
import me.twerknation28.moonlight.manager.HoleManager;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import me.twerknation28.moonlight.util.RenderUtil;
import java.awt.Color;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Box;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.client.util.math.MatrixStack;
import me.twerknation28.moonlight.event.impl.Render3DEvent;
import net.minecraft.network.packet.Packet;
import me.twerknation28.moonlight.event.impl.PacketEvent;
import net.minecraft.util.Hand;
import me.twerknation28.moonlight.event.impl.AttackBlockEvent;
import me.twerknation28.moonlight.event.EventListener;
import java.util.PriorityQueue;
import java.util.Iterator;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.entity.Entity;
import me.twerknation28.moonlight.manager.FriendManager;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.util.math.Direction;
import me.twerknation28.moonlight.manager.InteractionManager;
import me.twerknation28.moonlight.manager.PositionManager;
import me.twerknation28.moonlight.manager.InventoryManager;
import me.twerknation28.moonlight.util.EvictingQueue;
import me.twerknation28.moonlight.features.api.Category;
import java.util.Deque;
import me.twerknation28.moonlight.features.settings.Setting;
import me.twerknation28.moonlight.features.modules.Module;

public class AutoMine extends Module
{
    private static AutoMine INSTANCE;
    public final Setting<Boolean> multitaskConfig;
    public final Setting<Boolean> autoConfig;
    public final Setting<Boolean> autoRemineConfig;
    public final Setting<Boolean> strictDirectionConfig;
    public final Setting<Float> enemyRangeConfig;
    public final Setting<Boolean> doubleBreakConfig;
    public final Setting<Boolean> safetyConfig;
    public final Setting<Float> rangeConfig;
    public final Setting<Float> speedConfig;
    public final Setting<Boolean> rotateConfig;
    public final Setting<Boolean> switchResetConfig;
    public final Setting<Boolean> instantConfig;
    public final Setting<Boolean> headFreeConfig;
    public final Setting<Double> linewidthConfig;
    public final Setting<Integer> alphaConfig;
    public final Setting<Integer> lineAlphaConfig;
    private Deque<MiningData> miningQueue;
    private long lastBreak;
    private boolean manualOverride;
    
    public AutoMine() {
        super("AutoMine", "Mines blocks for you...", Category.COMBAT, true, false, true);
        this.multitaskConfig = this.register(new Setting<Boolean>("Multitask", false));
        this.autoConfig = this.register(new Setting<Boolean>("Auto", true));
        this.autoRemineConfig = this.register(new Setting<Boolean>("AutoRemine", true, v -> this.autoConfig.getValue()));
        this.strictDirectionConfig = this.register(new Setting<Boolean>("StrictDirection", false, v -> this.autoConfig.getValue()));
        this.enemyRangeConfig = this.register(new Setting<Float>("EnemyRange", 5.0f, 1.0f, 10.0f, 0.1f, v -> this.autoConfig.getValue()));
        this.doubleBreakConfig = this.register(new Setting<Boolean>("DoubleBreak", true));
        this.safetyConfig = this.register(new Setting<Boolean>("Safety", true));
        this.rangeConfig = this.register(new Setting<Float>("Range", 4.0f, 0.1f, 5.0f, 0.1f));
        this.speedConfig = this.register(new Setting<Float>("Speed", 1.0f, 0.1f, 1.0f, 0.1f));
        this.rotateConfig = this.register(new Setting<Boolean>("Rotate", true));
        this.switchResetConfig = this.register(new Setting<Boolean>("SwitchReset", false));
        this.instantConfig = this.register(new Setting<Boolean>("Instant", true));
        this.headFreeConfig = this.register(new Setting<Boolean>("FreeHead", true));
        this.linewidthConfig = this.register(new Setting<Double>("Linewidth", 2.0, 0.1, 4.0, 0.1));
        this.alphaConfig = this.register(new Setting<Integer>("Box Alpha", 20, 0, 255, 1));
        this.lineAlphaConfig = this.register(new Setting<Integer>("Line Alpha", 70, 0, 255, 1));
        this.miningQueue = new EvictingQueue<MiningData>(2);
        this.setInstance();
    }
    
    public static AutoMine getInstance() {
        if (AutoMine.INSTANCE == null) {
            AutoMine.INSTANCE = new AutoMine();
        }
        return AutoMine.INSTANCE;
    }
    
    private void setInstance() {
        AutoMine.INSTANCE = this;
    }
    
    @Override
    public void onEnable() {
        if (this.doubleBreakConfig.getValue()) {
            this.miningQueue = new EvictingQueue<MiningData>(2);
        }
        else {
            this.miningQueue = new EvictingQueue<MiningData>(1);
        }
    }
    
    @Override
    public void onDisable() {
        this.miningQueue.clear();
        this.manualOverride = false;
        InventoryManager.syncToClient();
    }
    
    @EventListener
    @Override
    public void onTick() {
        InventoryManager.syncToClient();
        MiningData miningData = null;
        if (!this.miningQueue.isEmpty()) {
            miningData = this.miningQueue.getFirst();
        }
        if (AutoMine.mc.player.isInSwimmingPose() && this.headFreeConfig.getValue()) {
            final BlockPos headBreakPos = PositionManager.getPlayerPos().up(1);
            if (AutoMine.mc.world.getBlockState(headBreakPos).getBlock().getHardness() != -1.0f && !AutoMine.mc.world.getBlockState(headBreakPos).isAir() && !AutoMine.mc.player.isCreative()) {
                final MiningData headBreak = new MiningData(headBreakPos, this.strictDirectionConfig.getValue() ? InteractionManager.getPlaceDirectionGrim(headBreakPos) : Direction.UP);
                this.overrideSet(headBreak);
            }
        }
        if (this.autoConfig.getValue() && !this.manualOverride && (miningData == null || AutoMine.mc.world.isAir(miningData.getPos()))) {
            PlayerEntity playerTarget = null;
            double distance = 3.4028234663852886E38;
            for (final PlayerEntity entity : AutoMine.mc.world.getPlayers()) {
                final AbstractClientPlayerEntity abstractClientPlayerEntity = (AbstractClientPlayerEntity)entity;
                if (entity != AutoMine.mc.player && !FriendManager.isFriend(entity.getName().getString())) {
                    final double dist = AutoMine.mc.player.distanceTo((Entity)entity);
                    if (dist > this.enemyRangeConfig.getValue() || dist >= distance) {
                        continue;
                    }
                    distance = dist;
                    playerTarget = entity;
                }
            }
            if (playerTarget != null) {
                final PriorityQueue<AutoMineCalc> miningPositions = this.getMiningPosition(playerTarget);
                final PriorityQueue<AutoMineCalc> miningPositionsNoAir = this.getNoAir(miningPositions);
                final PriorityQueue<AutoMineCalc> cityPositions = this.autoRemineConfig.getValue() ? miningPositions : miningPositionsNoAir;
                if (cityPositions.isEmpty()) {
                    return;
                }
                if (this.doubleBreakConfig.getValue()) {
                    final AutoMineCalc cityPos = cityPositions.poll();
                    if (cityPos != null) {
                        miningPositionsNoAir.remove(cityPos);
                        BlockPos cityPos2 = null;
                        if (!miningPositionsNoAir.isEmpty()) {
                            cityPos2 = miningPositionsNoAir.poll().pos();
                        }
                        if (cityPos2 != null && cityPos.pos() != cityPos2) {
                            if (!AutoMine.mc.world.isAir(cityPos.pos()) && !AutoMine.mc.world.isAir(cityPos2) && !this.isBlockDelayGrim()) {
                                final AutoMiningData data1 = new AutoMiningData(cityPos2, this.strictDirectionConfig.getValue() ? InteractionManager.getPlaceDirectionGrim(cityPos2) : Direction.UP);
                                final MiningData data2 = new AutoMiningData(cityPos.pos(), this.strictDirectionConfig.getValue() ? InteractionManager.getPlaceDirectionGrim(cityPos.pos()) : Direction.UP);
                                this.startMining(data1);
                                this.startMining(data2);
                                this.miningQueue.addFirst(data1);
                                this.miningQueue.addFirst(data2);
                            }
                        }
                        else if (!AutoMine.mc.world.isAir(cityPos.pos()) && !this.isBlockDelayGrim()) {
                            final AutoMiningData data1 = new AutoMiningData(cityPos.pos(), this.strictDirectionConfig.getValue() ? InteractionManager.getPlaceDirectionGrim(cityPos.pos()) : Direction.UP);
                            this.startMining(data1);
                            this.miningQueue.addFirst(data1);
                        }
                    }
                }
                else {
                    final AutoMineCalc cityPos = cityPositions.poll();
                    if (cityPos != null && !this.isBlockDelayGrim()) {
                        if (miningData instanceof AutoMiningData && miningData.isInstantRemine() && !AutoMine.mc.world.isAir(miningData.getPos()) && this.autoRemineConfig.getValue()) {
                            this.stopMining(miningData);
                            InventoryManager.syncToClient();
                        }
                        else if (!AutoMine.mc.world.isAir(cityPos.pos()) && !this.isBlockDelayGrim()) {
                            final MiningData data3 = new AutoMiningData(cityPos.pos(), this.strictDirectionConfig.getValue() ? InteractionManager.getPlaceDirectionGrim(cityPos.pos()) : Direction.UP);
                            this.startMining(data3);
                            this.miningQueue.addFirst(data3);
                        }
                    }
                }
            }
        }
        if (!this.miningQueue.isEmpty()) {
            for (final MiningData data4 : this.miningQueue) {
                if (data4.getState().isAir()) {
                    InventoryManager.swapBack();
                    if (this.isDataPacketMine(data4)) {
                        this.miningQueue.remove(data4);
                        return;
                    }
                }
                final float damageDelta = InteractionManager.calcBlockBreakingDelta(data4.getState(), (BlockView)AutoMine.mc.world, data4.getPos());
                data4.damage(damageDelta);
                if (data4.getBlockDamage() >= 1.0f && this.isDataPacketMine(data4)) {
                    if (AutoMine.mc.player.isUsingItem() && !this.multitaskConfig.getValue()) {
                        return;
                    }
                    if (data4.getSlot() == -1) {
                        continue;
                    }
                    InventoryManager.setSlotLoud(data4.getSlot());
                }
            }
            final MiningData miningData2 = this.miningQueue.getFirst();
            if (miningData2 != null) {
                final double distance = AutoMine.mc.player.getEyePos().squaredDistanceTo(miningData2.getPos().toCenterPos());
                if (distance > this.rangeConfig.getValue() * (double)this.rangeConfig.getValue()) {
                    this.miningQueue.remove(miningData2);
                    return;
                }
                if (miningData2.getState().isAir()) {
                    if (this.manualOverride) {
                        this.manualOverride = false;
                        this.miningQueue.remove(miningData2);
                        return;
                    }
                    if (this.instantConfig.getValue()) {
                        if (miningData2 instanceof AutoMiningData && !this.autoRemineConfig.getValue()) {
                            this.miningQueue.remove(miningData2);
                            return;
                        }
                        miningData2.setInstantRemine();
                        miningData2.setDamage(1.0f);
                    }
                    else {
                        miningData2.resetDamage();
                    }
                    return;
                }
                else if (miningData2.getBlockDamage() >= this.speedConfig.getValue() || miningData2.isInstantRemine()) {
                    if (AutoMine.mc.player.isUsingItem() && !this.multitaskConfig.getValue()) {
                        return;
                    }
                    this.stopMining(miningData2);
                    InventoryManager.syncToClient();
                }
            }
        }
        InventoryManager.syncToClient();
    }
    
    @EventListener
    @Override
    public void onAttackBlock(final AttackBlockEvent event) {
        if (event.getState().getBlock().getHardness() != -1.0f && !event.getState().isAir() && !AutoMine.mc.player.isCreative()) {
            event.cancel();
            final MiningData miningData = new MiningData(event.getPos(), event.getDirection());
            this.overrideSet(miningData);
        }
    }
    
    public void overrideSet(final MiningData miningData) {
        final int queueSize = this.miningQueue.size();
        if (queueSize == 0) {
            this.attemptMine(miningData.getPos(), miningData.getDirection());
        }
        else if (queueSize == 1) {
            final MiningData data1 = this.miningQueue.getFirst();
            if (data1.getPos().equals((Object)miningData.getPos())) {
                return;
            }
            if (data1 instanceof AutoMiningData) {
                this.manualOverride = true;
            }
            this.attemptMine(miningData.getPos(), miningData.getDirection());
        }
        else if (queueSize == 2) {
            final MiningData data1 = this.miningQueue.getFirst();
            final MiningData data2 = this.miningQueue.getLast();
            if (data1.getPos().equals((Object)miningData.getPos()) || data2.getPos().equals((Object)miningData.getPos())) {
                return;
            }
            if (data1 instanceof AutoMiningData || data2 instanceof AutoMiningData) {
                this.manualOverride = true;
            }
            this.attemptMine(miningData.getPos(), miningData.getDirection());
        }
        AutoMine.mc.player.swingHand(Hand.MAIN_HAND);
    }
    
    @EventListener
    @Override
    public void onPacketSend(final PacketEvent.Send event) {
        if (event.getPacket() instanceof Packet && this.switchResetConfig.getValue()) {
            for (final MiningData data : this.miningQueue) {
                data.resetDamage();
            }
        }
    }
    
    @EventListener
    @Override
    public void onRender3D(final Render3DEvent event) {
        for (final MiningData data : this.miningQueue) {
            this.renderMiningData(event.getMatrixStack(), data);
        }
    }
    
    private void renderMiningData(final MatrixStack matrixStack, final MiningData data) {
        if (data != null && !AutoMine.mc.player.isCreative() && data.getBlockDamage() > 0.01f) {
            final float miningSpeed = this.isDataPacketMine(data) ? 1.0f : this.speedConfig.getValue();
            final BlockPos mining = data.getPos();
            VoxelShape outlineShape = VoxelShapes.fullCube();
            if (!data.isInstantRemine()) {
                outlineShape = data.getState().getOutlineShape((BlockView)AutoMine.mc.world, mining);
                outlineShape = (outlineShape.isEmpty() ? VoxelShapes.fullCube() : outlineShape);
            }
            final Box render1 = outlineShape.getBoundingBox();
            final Box render2 = new Box(mining.getX() + render1.minX, mining.getY() + render1.minY, mining.getZ() + render1.minZ, mining.getX() + render1.maxX, mining.getY() + render1.maxY, mining.getZ() + render1.maxZ);
            final Vec3d center = render2.getCenter();
            final float scale = MathHelper.clamp(data.getBlockDamage() / miningSpeed, 0.0f, 1.0f);
            final double dx = (render1.maxX - render1.minX) / 2.0;
            final double dy = (render1.maxY - render1.minY) / 2.0;
            final double dz = (render1.maxZ - render1.minZ) / 2.0;
            final Box scaled = new Box(center, center).expand(dx * scale, dy * scale, dz * scale);
            final int colourint = (data.getBlockDamage() > 0.95f * miningSpeed) ? 1610678016 : 1627324416;
            final Color colour = new Color(ColorHelper.Argb.getRed(colourint), ColorHelper.Argb.getGreen(colourint), ColorHelper.Argb.getBlue(colourint), this.alphaConfig.getValue());
            final Color linecolour = new Color(ColorHelper.Argb.getRed(colourint), ColorHelper.Argb.getGreen(colourint), ColorHelper.Argb.getBlue(colourint), this.lineAlphaConfig.getValue());
            RenderUtil.drawBoxFilled(matrixStack, scaled, colour);
            RenderUtil.drawBox(matrixStack, scaled, linecolour, this.linewidthConfig.getValue());
        }
    }
    
    private PriorityQueue<AutoMineCalc> getNoAir(final PriorityQueue<AutoMineCalc> calcs) {
        final PriorityQueue<AutoMineCalc> noAir = new PriorityQueue<AutoMineCalc>();
        for (final AutoMineCalc calc : calcs) {
            if (!AutoMine.mc.world.isAir(calc.pos())) {
                noAir.add(calc);
            }
        }
        return noAir;
    }
    
    private PriorityQueue<AutoMineCalc> getMiningPosition(final PlayerEntity entity) {
        final List<BlockPos> entityIntersections = HoleManager.getSurroundEntities((Entity)entity);
        final PriorityQueue<AutoMineCalc> miningPositions = new PriorityQueue<AutoMineCalc>();
        for (final BlockPos blockPos : entityIntersections) {
            final double dist = AutoMine.mc.player.getEyePos().squaredDistanceTo(blockPos.toCenterPos());
            if (dist <= this.rangeConfig.getValue() * (double)this.rangeConfig.getValue() && !AutoMine.mc.world.getBlockState(blockPos).isReplaceable() && !this.isSelfBlock(blockPos)) {
                miningPositions.add(new AutoMineCalc(blockPos, Double.MAX_VALUE));
            }
        }
        final List<BlockPos> surroundBlocks = HoleManager.getEntitySurroundNoSupport((Entity)entity);
        for (final BlockPos blockPos2 : surroundBlocks) {
            final double dist2 = AutoMine.mc.player.getEyePos().squaredDistanceTo(blockPos2.toCenterPos());
            if (dist2 <= this.rangeConfig.getValue() * (double)this.rangeConfig.getValue()) {
                final double damage = 15.0;
                if (this.isSelfBlock(blockPos2)) {
                    continue;
                }
                miningPositions.add(new AutoMineCalc(blockPos2, damage));
            }
        }
        return miningPositions;
    }
    
    private void attemptMine(final BlockPos pos, final Direction direction) {
        if (!this.isBlockDelayGrim()) {
            final MiningData miningData = new MiningData(pos, direction);
            this.startMining(miningData);
            this.miningQueue.addFirst(miningData);
        }
    }
    
    private void startMining(final MiningData data) {
        if (!data.getState().isAir() && !data.isStarted()) {
            NetworkManager.sendSequencedPacket(id -> new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK, data.getPos(), data.getDirection(), id));
            NetworkManager.sendSequencedPacket(id -> new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.START_DESTROY_BLOCK, data.getPos(), data.getDirection(), id));
            if (this.doubleBreakConfig.getValue()) {
                NetworkManager.sendSequencedPacket(id -> new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK, data.getPos(), data.getDirection(), id));
            }
            data.setStarted();
        }
    }
    
    private void abortMining(final MiningData data) {
        if (data.isStarted() && !data.getState().isAir() && !data.isInstantRemine() && data.getBlockDamage() < 1.0f) {
            NetworkManager.sendSequencedPacket(id -> new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.ABORT_DESTROY_BLOCK, data.getPos(), data.getDirection(), id));
            InventoryManager.syncToClient();
        }
    }
    
    private boolean isSelfBlock(final BlockPos target) {
        return this.safetyConfig.getValue() && target.equals((Object)PositionManager.getPlayerPos());
    }
    
    private void stopMining(final MiningData data) {
        if (data.isStarted() && !data.getState().isAir()) {
            final boolean canSwap = data.getSlot() != -1;
            if (canSwap) {
                InventoryManager.setSlotLoud(data.getSlot());
            }
            if (this.rotateConfig.getValue()) {
                final float[] rotations = RotationManager.getRotationsTo(AutoMine.mc.player.getEyePos(), data.getPos().toCenterPos());
                RotationManager.setRotationSilent(rotations[0], rotations[1], true);
            }
            NetworkManager.sendSequencedPacket(id -> new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK, data.getPos(), data.getDirection(), id));
            this.lastBreak = System.currentTimeMillis();
            if (canSwap) {
                InventoryManager.syncToClient();
            }
            if (this.rotateConfig.getValue()) {
                RotationManager.setRotationSilentSync(true);
            }
        }
    }
    
    private boolean isDataPacketMine(final MiningData data) {
        return this.miningQueue.size() == 2 && data == this.miningQueue.getLast();
    }
    
    public boolean isBlockDelayGrim() {
        return System.currentTimeMillis() - this.lastBreak <= 280L;
    }
    
    static {
        AutoMine.INSTANCE = new AutoMine();
    }
    
    public static class MiningData
    {
        private final BlockPos pos;
        private final Direction direction;
        private float blockDamage;
        private boolean instantRemine;
        private boolean started;
        
        public MiningData(final BlockPos pos, final Direction direction) {
            this.pos = pos;
            this.direction = direction;
        }
        
        public boolean isInstantRemine() {
            return this.instantRemine;
        }
        
        public void setInstantRemine() {
            this.instantRemine = true;
        }
        
        public float damage(final float dmg) {
            return this.blockDamage += dmg;
        }
        
        public void setDamage(final float blockDamage) {
            this.blockDamage = blockDamage;
        }
        
        public void resetDamage() {
            this.instantRemine = false;
            this.blockDamage = 0.0f;
        }
        
        public BlockPos getPos() {
            return this.pos;
        }
        
        public Direction getDirection() {
            return this.direction;
        }
        
        public int getSlot() {
            return InventoryManager.getBestToolNoFallback(this.getState());
        }
        
        public BlockState getState() {
            return Util.mc.world.getBlockState(this.pos);
        }
        
        public boolean isStarted() {
            return this.started;
        }
        
        public void setStarted() {
            this.started = true;
        }
        
        public float getBlockDamage() {
            return this.blockDamage;
        }
    }
    
    record AutoMineCalc(BlockPos pos, double entityDamage) implements Comparable<AutoMineCalc> {
        @Override
        public int compareTo(@NotNull final AutoMineCalc o) {
            return Double.compare(-this.entityDamage(), -o.entityDamage());
        }
    }
    
    public static class AutoMiningData extends MiningData
    {
        public AutoMiningData(final BlockPos pos, final Direction direction) {
            super(pos, direction);
        }
    }
}
