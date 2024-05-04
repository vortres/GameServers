package me.vortres.entity;

import net.minestom.server.attribute.Attribute;
import net.minestom.server.entity.EntityCreature;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.ai.EntityAIGroupBuilder;
import net.minestom.server.entity.ai.goal.DoNothingGoal;
import net.minestom.server.entity.ai.goal.RandomLookAroundGoal;
import net.minestom.server.entity.ai.goal.RandomStrollGoal;
import net.minestom.server.entity.metadata.animal.ChickenMeta;

import java.util.concurrent.ThreadLocalRandom;

public class ChickenEntity extends EntityCreature {

    public ChickenEntity() {
        super(EntityType.CHICKEN);

        addAIGroup(
                new EntityAIGroupBuilder()
                        .addGoalSelector(new RandomLookAroundGoal(this, 4))
                        .addGoalSelector(new RandomStrollGoal(this, 2))
                        .addGoalSelector(new DoNothingGoal(this, 500, 0.1F))
                        .build()
        );

        boolean isBaby = ThreadLocalRandom.current().nextBoolean();
        ((ChickenMeta) entityMeta).setBaby(isBaby);
        if (isBaby) {
            getAttribute(Attribute.MAX_HEALTH).setBaseValue(getMaxHealth() / 2);
            heal();
        }

        getAttribute(Attribute.MOVEMENT_SPEED).setBaseValue(0.1f);
    }
}