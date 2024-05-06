package dev.vortres.lobby.entity;

import net.minestom.server.attribute.Attribute;
import net.minestom.server.entity.EntityCreature;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.LivingEntity;
import net.minestom.server.entity.ai.EntityAIGroupBuilder;
import net.minestom.server.entity.ai.goal.DoNothingGoal;
import net.minestom.server.entity.ai.goal.MeleeAttackGoal;
import net.minestom.server.entity.ai.goal.RandomLookAroundGoal;
import net.minestom.server.entity.ai.goal.RandomStrollGoal;
import net.minestom.server.entity.ai.target.ClosestEntityTarget;
import net.minestom.server.entity.ai.target.LastEntityDamagerTarget;
import net.minestom.server.entity.metadata.monster.zombie.ZombieMeta;
import net.minestom.server.utils.time.TimeUnit;

import java.util.concurrent.ThreadLocalRandom;

public class ZombieEntity extends EntityCreature {

    public ZombieEntity() {
        super(EntityType.ZOMBIE);

        addAIGroup(
                new EntityAIGroupBuilder()
                        .addGoalSelector(new DoNothingGoal(this, 500, 0.1F))
                        .addGoalSelector(new RandomLookAroundGoal(this, 4))
                        .addGoalSelector(new MeleeAttackGoal(this, 500, 2, TimeUnit.SERVER_TICK))
                        .addGoalSelector(new RandomStrollGoal(this, 2))
                        .addTargetSelector(new LastEntityDamagerTarget(this, 15))
                        .addTargetSelector(new ClosestEntityTarget(this, 15, LivingEntity.class))
                        .build()
        );

        boolean isBaby = ThreadLocalRandom.current().nextBoolean();
        ((ZombieMeta) entityMeta).setBaby(isBaby);
        if (isBaby) {
            getAttribute(Attribute.MAX_HEALTH).setBaseValue(getMaxHealth() / 2);
            heal();
        }
    }
}