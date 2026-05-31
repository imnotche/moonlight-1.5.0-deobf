package me.twerknation28.moonlight.features.modules.combat;

import java.awt.Color;
import java.util.Deque;
import java.util.List;
import java.util.PriorityQueue;
import me.twerknation28.moonlight.event.EventListener;
import me.twerknation28.moonlight.event.impl.AttackBlockEvent;
import me.twerknation28.moonlight.event.impl.PacketEvent;
import me.twerknation28.moonlight.event.impl.Render3DEvent;
import me.twerknation28.moonlight.features.api.Category;
import me.twerknation28.moonlight.features.modules.Module;
import me.twerknation28.moonlight.features.settings.Setting;
import me.twerknation28.moonlight.manager.FriendManager;
import me.twerknation28.moonlight.manager.HoleManager;
import me.twerknation28.moonlight.manager.InteractionManager;
import me.twerknation28.moonlight.manager.InventoryManager;
import me.twerknation28.moonlight.manager.NetworkManager;
import me.twerknation28.moonlight.manager.PositionManager;
import me.twerknation28.moonlight.manager.RotationManager;
import me.twerknation28.moonlight.util.EvictingQueue;
import me.twerknation28.moonlight.util.RenderUtil;
import me.twerknation28.moonlight.util.Util;
import net.minecraft.block.BlockState;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import org.jetbrains.annotations.NotNull;

public class AutoMine
extends Module {
    private static AutoMine INSTANCE = new AutoMine();
    public final Setting<Boolean> multitaskConfig = this.register(new Setting<Boolean>("Multitask", false));
    public final Setting<Boolean> autoConfig = this.register(new Setting<Boolean>("Auto", true));
    public final Setting<Boolean> autoRemineConfig = this.register(new Setting<Boolean>("AutoRemine", Boolean.valueOf(true), v -> this.autoConfig.getValue()));
    public final Setting<Boolean> strictDirectionConfig = this.register(new Setting<Boolean>("StrictDirection", Boolean.valueOf(false), v -> this.autoConfig.getValue()));
    public final Setting<Float> enemyRangeConfig = this.register(new Setting<Float>("EnemyRange", Float.valueOf(5.0f), Float.valueOf(1.0f), Float.valueOf(10.0f), Float.valueOf(0.1f), v -> this.autoConfig.getValue()));
    public final Setting<Boolean> doubleBreakConfig = this.register(new Setting<Boolean>("DoubleBreak", true));
    public final Setting<Boolean> safetyConfig = this.register(new Setting<Boolean>("Safety", true));
    public final Setting<Float> rangeConfig = this.register(new Setting<Float>("Range", Float.valueOf(4.0f), Float.valueOf(0.1f), Float.valueOf(5.0f), Float.valueOf(0.1f)));
    public final Setting<Float> speedConfig = this.register(new Setting<Float>("Speed", Float.valueOf(1.0f), Float.valueOf(0.1f), Float.valueOf(1.0f), Float.valueOf(0.1f)));
    public final Setting<Boolean> rotateConfig = this.register(new Setting<Boolean>("Rotate", true));
    public final Setting<Boolean> switchResetConfig = this.register(new Setting<Boolean>("SwitchReset", false));
    public final Setting<Boolean> instantConfig = this.register(new Setting<Boolean>("Instant", true));
    public final Setting<Boolean> headFreeConfig = this.register(new Setting<Boolean>("FreeHead", true));
    public final Setting<Double> linewidthConfig = this.register(new Setting<Double>("Linewidth", 2.0, 0.1, 4.0, 0.1));
    public final Setting<Integer> alphaConfig = this.register(new Setting<Integer>("Box Alpha", 20, 0, 255, 1));
    public final Setting<Integer> lineAlphaConfig = this.register(new Setting<Integer>("Line Alpha", 70, 0, 255, 1));
    private Deque<MiningData> miningQueue = new EvictingQueue<MiningData>(2);
    private long lastBreak;
    private boolean manualOverride;

    public AutoMine() {
        super("AutoMine", "Mines blocks for you...", Category.COMBAT, true, false, true);
        this.setInstance();
    }

    public static AutoMine getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new AutoMine();
        }
        return INSTANCE;
    }

    private void setInstance() {
        INSTANCE = this;
    }

    @Override
    public void onEnable() {
        this.miningQueue = this.doubleBreakConfig.getValue() != false ? new EvictingQueue<MiningData>(2) : new EvictingQueue<MiningData>(1);
    }

    @Override
    public void onDisable() {
        this.miningQueue.clear();
        this.manualOverride = false;
        InventoryManager.syncToClient();
    }

    @Override
    @EventListener
    public void onTick() {
        BlockPos headBreakPos;
        InventoryManager.syncToClient();
        MiningData miningData = null;
        if (!this.miningQueue.isEmpty()) {
            miningData = this.miningQueue.getFirst();
        }
        if (AutoMine.mc.player.isInSwimmingPose() && this.headFreeConfig.getValue().booleanValue() && AutoMine.mc.world.getBlockState(headBreakPos = PositionManager.getPlayerPos().up(1)).getBlock().getHardness() != -1.0f && !AutoMine.mc.world.getBlockState(headBreakPos).isAir() && !AutoMine.mc.player.isCreative()) {
            MiningData headBreak = new MiningData(headBreakPos, this.strictDirectionConfig.getValue() != false ? InteractionManager.getPlaceDirectionGrim(headBreakPos) : Direction.UP);
            this.overrideSet(headBreak);
        }
        if (this.autoConfig.getValue().booleanValue() && !this.manualOverride && (miningData == null || AutoMine.mc.world.isAir(miningData.getPos()))) {
            AbstractClientPlayerEntity playerTarget = null;
            double distance = 3.4028234663852886E38;
            for (AbstractClientPlayerEntity abstractClientPlayerEntity : AutoMine.mc.world.getPlayers()) {
                double dist;
                AbstractClientPlayerEntity entity = abstractClientPlayerEntity;
                if (entity == AutoMine.mc.player || FriendManager.isFriend(entity.getName().getString()) || (dist = (double)AutoMine.mc.player.distanceTo((Entity)entity)) > (double)this.enemyRangeConfig.getValue().floatValue() || !(dist < distance)) continue;
                distance = dist;
                playerTarget = entity;
            }
            if (playerTarget != null) {
                PriorityQueue<AutoMineCalc> cityPositions;
                PriorityQueue<AutoMineCalc> miningPositions = this.getMiningPosition((PlayerEntity)playerTarget);
                PriorityQueue<AutoMineCalc> miningPositionsNoAir = this.getNoAir(miningPositions);
                PriorityQueue<AutoMineCalc> priorityQueue = cityPositions = this.autoRemineConfig.getValue() != false ? miningPositions : miningPositionsNoAir;
                if (cityPositions.isEmpty()) {
                    return;
                }
                if (this.doubleBreakConfig.getValue().booleanValue()) {
                    AutoMineCalc cityPos = cityPositions.poll();
                    if (cityPos != null) {
                        miningPositionsNoAir.remove(cityPos);
                        BlockPos cityPos2 = null;
                        if (!miningPositionsNoAir.isEmpty()) {
                            cityPos2 = miningPositionsNoAir.poll().pos();
                        }
                        if (cityPos2 != null && cityPos.pos() != cityPos2) {
                            if (!(AutoMine.mc.world.isAir(cityPos.pos()) || AutoMine.mc.world.isAir(cityPos2) || this.isBlockDelayGrim())) {
                                data1 = new AutoMiningData(cityPos2, this.strictDirectionConfig.getValue() != false ? InteractionManager.getPlaceDirectionGrim(cityPos2) : Direction.UP);
                                AutoMiningData data2 = new AutoMiningData(cityPos.pos(), this.strictDirectionConfig.getValue() != false ? InteractionManager.getPlaceDirectionGrim(cityPos.pos()) : Direction.UP);
                                this.startMining(data1);
                                this.startMining(data2);
                                this.miningQueue.addFirst(data1);
                                this.miningQueue.addFirst(data2);
                            }
                        } else if (!AutoMine.mc.world.isAir(cityPos.pos()) && !this.isBlockDelayGrim()) {
                            data1 = new AutoMiningData(cityPos.pos(), this.strictDirectionConfig.getValue() != false ? InteractionManager.getPlaceDirectionGrim(cityPos.pos()) : Direction.UP);
                            this.startMining(data1);
                            this.miningQueue.addFirst(data1);
                        }
                    }
                } else {
                    AutoMineCalc cityPos = cityPositions.poll();
                    if (cityPos != null && !this.isBlockDelayGrim()) {
                        if (miningData instanceof AutoMiningData && miningData.isInstantRemine() && !AutoMine.mc.world.isAir(miningData.getPos()) && this.autoRemineConfig.getValue().booleanValue()) {
                            this.stopMining(miningData);
                            InventoryManager.syncToClient();
                        } else if (!AutoMine.mc.world.isAir(cityPos.pos()) && !this.isBlockDelayGrim()) {
                            AutoMiningData data = new AutoMiningData(cityPos.pos(), this.strictDirectionConfig.getValue() != false ? InteractionManager.getPlaceDirectionGrim(cityPos.pos()) : Direction.UP);
                            this.startMining(data);
                            this.miningQueue.addFirst(data);
                        }
                    }
                }
            }
        }
        if (!this.miningQueue.isEmpty()) {
            for (MiningData data : this.miningQueue) {
                if (data.getState().isAir()) {
                    InventoryManager.swapBack();
                    if (this.isDataPacketMine(data)) {
                        this.miningQueue.remove(data);
                        return;
                    }
                }
                float damageDelta = InteractionManager.calcBlockBreakingDelta(data.getState(), (BlockView)AutoMine.mc.world, data.getPos());
                data.damage(damageDelta);
                if (!(data.getBlockDamage() >= 1.0f) || !this.isDataPacketMine(data)) continue;
                if (AutoMine.mc.player.isUsingItem() && !this.multitaskConfig.getValue().booleanValue()) {
                    return;
                }
                if (data.getSlot() == -1) continue;
                InventoryManager.setSlotLoud(data.getSlot());
            }
            MiningData miningData2 = this.miningQueue.getFirst();
            if (miningData2 != null) {
                double distance = AutoMine.mc.player.getEyePos().squaredDistanceTo(miningData2.getPos().toCenterPos());
                if (distance > this.rangeConfig.getValue().doubleValue() * this.rangeConfig.getValue().doubleValue()) {
                    this.miningQueue.remove(miningData2);
                    return;
                }
                if (miningData2.getState().isAir()) {
                    if (this.manualOverride) {
                        this.manualOverride = false;
                        this.miningQueue.remove(miningData2);
                        return;
                    }
                    if (this.instantConfig.getValue().booleanValue()) {
                        if (miningData2 instanceof AutoMiningData && !this.autoRemineConfig.getValue().booleanValue()) {
                            this.miningQueue.remove(miningData2);
                            return;
                        }
                        miningData2.setInstantRemine();
                        miningData2.setDamage(1.0f);
                    } else {
                        miningData2.resetDamage();
                    }
                    return;
                }
                if (miningData2.getBlockDamage() >= this.speedConfig.getValue().floatValue() || miningData2.isInstantRemine()) {
                    if (AutoMine.mc.player.isUsingItem() && !this.multitaskConfig.getValue().booleanValue()) {
                        return;
                    }
                    this.stopMining(miningData2);
                    InventoryManager.syncToClient();
                }
            }
        }
        InventoryManager.syncToClient();
    }

    @Override
    @EventListener
    public void onAttackBlock(AttackBlockEvent event) {
        if (event.getState().getBlock().getHardness() != -1.0f && !event.getState().isAir() && !AutoMine.mc.player.isCreative()) {
            event.cancel();
            MiningData miningData = new MiningData(event.getPos(), event.getDirection());
            this.overrideSet(miningData);
        }
    }

    public void overrideSet(MiningData miningData) {
        int queueSize = this.miningQueue.size();
        if (queueSize == 0) {
            this.attemptMine(miningData.getPos(), miningData.getDirection());
        } else if (queueSize == 1) {
            MiningData data1 = this.miningQueue.getFirst();
            if (data1.getPos().equals((Object)miningData.getPos())) {
                return;
            }
            if (data1 instanceof AutoMiningData) {
                this.manualOverride = true;
            }
            this.attemptMine(miningData.getPos(), miningData.getDirection());
        } else if (queueSize == 2) {
            MiningData data1 = this.miningQueue.getFirst();
            MiningData data2 = this.miningQueue.getLast();
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

    @Override
    @EventListener
    public void onPacketSend(PacketEvent.Send event) {
        if (event.getPacket() instanceof Packet && this.switchResetConfig.getValue().booleanValue()) {
            for (MiningData data : this.miningQueue) {
                data.resetDamage();
            }
        }
    }

    @Override
    @EventListener
    public void onRender3D(Render3DEvent event) {
        for (MiningData data : this.miningQueue) {
            this.renderMiningData(event.getMatrixStack(), data);
        }
    }

    private void renderMiningData(MatrixStack matrixStack, MiningData data) {
        if (data != null && !AutoMine.mc.player.isCreative() && data.getBlockDamage() > 0.01f) {
            float miningSpeed = this.isDataPacketMine(data) ? 1.0f : this.speedConfig.getValue().floatValue();
            BlockPos mining = data.getPos();
            VoxelShape outlineShape = VoxelShapes.fullCube();
            if (!data.isInstantRemine()) {
                outlineShape = data.getState().getOutlineShape((BlockView)AutoMine.mc.world, mining);
                outlineShape = outlineShape.isEmpty() ? VoxelShapes.fullCube() : outlineShape;
            }
            Box render1 = outlineShape.getBoundingBox();
            Box render = new Box((double)mining.getX() + render1.minX, (double)mining.getY() + render1.minY, (double)mining.getZ() + render1.minZ, (double)mining.getX() + render1.maxX, (double)mining.getY() + render1.maxY, (double)mining.getZ() + render1.maxZ);
            Vec3d center = render.getCenter();
            float scale = MathHelper.clamp((float)(data.getBlockDamage() / miningSpeed), (float)0.0f, (float)1.0f);
            double dx = (render1.maxX - render1.minX) / 2.0;
            double dy = (render1.maxY - render1.minY) / 2.0;
            double dz = (render1.maxZ - render1.minZ) / 2.0;
            Box scaled = new Box(center, center).expand(dx * (double)scale, dy * (double)scale, dz * (double)scale);
            int colourint = data.getBlockDamage() > 0.95f * miningSpeed ? 0x6000FF00 : 0x60FF0000;
            Color colour = new Color(ColorHelper.Argb.getRed((int)colourint), ColorHelper.Argb.getGreen((int)colourint), ColorHelper.Argb.getBlue((int)colourint), this.alphaConfig.getValue());
            Color linecolour = new Color(ColorHelper.Argb.getRed((int)colourint), ColorHelper.Argb.getGreen((int)colourint), ColorHelper.Argb.getBlue((int)colourint), this.lineAlphaConfig.getValue());
            RenderUtil.drawBoxFilled(matrixStack, scaled, colour);
            RenderUtil.drawBox(matrixStack, scaled, linecolour, (double)this.linewidthConfig.getValue());
        }
    }

    private PriorityQueue<AutoMineCalc> getNoAir(PriorityQueue<AutoMineCalc> calcs) {
        PriorityQueue<AutoMineCalc> noAir = new PriorityQueue<AutoMineCalc>();
        for (AutoMineCalc calc : calcs) {
            if (AutoMine.mc.world.isAir(calc.pos())) continue;
            noAir.add(calc);
        }
        return noAir;
    }

    private PriorityQueue<AutoMineCalc> getMiningPosition(PlayerEntity entity) {
        List<BlockPos> entityIntersections = HoleManager.getSurroundEntities((Entity)entity);
        PriorityQueue<AutoMineCalc> miningPositions = new PriorityQueue<AutoMineCalc>();
        for (BlockPos blockPos : entityIntersections) {
            double dist = AutoMine.mc.player.getEyePos().squaredDistanceTo(blockPos.toCenterPos());
            if (dist > this.rangeConfig.getValue().doubleValue() * this.rangeConfig.getValue().doubleValue() || AutoMine.mc.world.getBlockState(blockPos).isReplaceable() || this.isSelfBlock(blockPos)) continue;
            miningPositions.add(new AutoMineCalc(blockPos, Double.MAX_VALUE));
        }
        List<BlockPos> surroundBlocks = HoleManager.getEntitySurroundNoSupport((Entity)entity);
        for (BlockPos blockPos : surroundBlocks) {
            double dist = AutoMine.mc.player.getEyePos().squaredDistanceTo(blockPos.toCenterPos());
            if (dist > this.rangeConfig.getValue().doubleValue() * this.rangeConfig.getValue().doubleValue()) continue;
            double damage = 15.0;
            if (this.isSelfBlock(blockPos)) continue;
            miningPositions.add(new AutoMineCalc(blockPos, damage));
        }
        return miningPositions;
    }

    private void attemptMine(BlockPos pos, Direction direction) {
        if (!this.isBlockDelayGrim()) {
            MiningData miningData = new MiningData(pos, direction);
            this.startMining(miningData);
            this.miningQueue.addFirst(miningData);
        }
    }

    private void startMining(MiningData data) {
        if (!data.getState().isAir() && !data.isStarted()) {
            NetworkManager.sendSequencedPacket(id -> new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK, data.getPos(), data.getDirection(), id));
            NetworkManager.sendSequencedPacket(id -> new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.START_DESTROY_BLOCK, data.getPos(), data.getDirection(), id));
            if (this.doubleBreakConfig.getValue().booleanValue()) {
                NetworkManager.sendSequencedPacket(id -> new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK, data.getPos(), data.getDirection(), id));
            }
            data.setStarted();
        }
    }

    private void abortMining(MiningData data) {
        if (data.isStarted() && !data.getState().isAir() && !data.isInstantRemine() && !(data.getBlockDamage() >= 1.0f)) {
            NetworkManager.sendSequencedPacket(id -> new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.ABORT_DESTROY_BLOCK, data.getPos(), data.getDirection(), id));
            InventoryManager.syncToClient();
        }
    }

    private boolean isSelfBlock(BlockPos target) {
        if (this.safetyConfig.getValue().booleanValue()) {
            return target.equals((Object)PositionManager.getPlayerPos());
        }
        return false;
    }

    private void stopMining(MiningData data) {
        if (data.isStarted() && !data.getState().isAir()) {
            boolean canSwap;
            boolean bl = canSwap = data.getSlot() != -1;
            if (canSwap) {
                InventoryManager.setSlotLoud(data.getSlot());
            }
            if (this.rotateConfig.getValue().booleanValue()) {
                float[] rotations = RotationManager.getRotationsTo(AutoMine.mc.player.getEyePos(), data.getPos().toCenterPos());
                RotationManager.setRotationSilent(rotations[0], rotations[1], true);
            }
            NetworkManager.sendSequencedPacket(id -> new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK, data.getPos(), data.getDirection(), id));
            this.lastBreak = System.currentTimeMillis();
            if (canSwap) {
                InventoryManager.syncToClient();
            }
            if (this.rotateConfig.getValue().booleanValue()) {
                RotationManager.setRotationSilentSync(true);
            }
        }
    }

    private boolean isDataPacketMine(MiningData data) {
        return this.miningQueue.size() == 2 && data == this.miningQueue.getLast();
    }

    public boolean isBlockDelayGrim() {
        return System.currentTimeMillis() - this.lastBreak <= 280L;
    }

    public static class MiningData {
        private final BlockPos pos;
        private final Direction direction;
        private float blockDamage;
        private boolean instantRemine;
        private boolean started;

        public MiningData(BlockPos pos, Direction direction) {
            this.pos = pos;
            this.direction = direction;
        }

        public boolean isInstantRemine() {
            return this.instantRemine;
        }

        public void setInstantRemine() {
            this.instantRemine = true;
        }

        public float damage(float dmg) {
            this.blockDamage += dmg;
            return this.blockDamage;
        }

        public void setDamage(float blockDamage) {
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

    private record AutoMineCalc(BlockPos pos, double entityDamage) implements Comparable<AutoMineCalc>
    {
        @Override
        public int compareTo(@NotNull AutoMineCalc o) {
            return Double.compare(-this.entityDamage(), -o.entityDamage());
        }
    }

    public static class AutoMiningData
    extends MiningData {
        public AutoMiningData(BlockPos pos, Direction direction) {
            super(pos, direction);
        }
    }
}
