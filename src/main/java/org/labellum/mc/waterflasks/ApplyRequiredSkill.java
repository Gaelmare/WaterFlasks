package org.labellum.mc.waterflasks;

import java.util.Random;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import net.dries007.tfc.util.skills.SkillTier;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.RandomValueRange;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraft.world.storage.loot.functions.LootFunction;

import net.dries007.tfc.api.capability.player.CapabilityPlayerData;
import net.dries007.tfc.api.capability.player.IPlayerData;
import net.dries007.tfc.util.skills.SimpleSkill;
import net.dries007.tfc.util.skills.SkillType;

@ParametersAreNonnullByDefault
public class ApplyRequiredSkill extends LootFunction
{
    private final SkillType<? extends SimpleSkill> skillType;
    private final SkillTier tier;
    private final float chanceAmount;

    private ApplyRequiredSkill(LootCondition[] conditionsIn, SkillType<? extends SimpleSkill> skillType, SkillTier tier, float chanceAmount)
    {
        super(conditionsIn);
        this.skillType = skillType;
        this.chanceAmount = chanceAmount;
        this.tier = tier;
    }

    @Override
    @Nonnull
    public ItemStack apply(ItemStack stack, Random rand, LootContext context)
    {
        Entity entity = context.getKillerPlayer();
        if (entity instanceof EntityPlayer)
        {
            IPlayerData skills = entity.getCapability(CapabilityPlayerData.CAPABILITY, null);
            if (skills != null)
            {
                SimpleSkill skill = skills.getSkill(this.skillType);
                if (skill != null && skill.getTier().isAtLeast(tier))
                {
                    //[0..1] - [0..1] < .5 for 50% chance. Since Adept = .25, setting chance to 25 means
                    // a 50% chance of drop at ADEPT, and 100% at MASTER
                    if (rand.nextDouble() - skill.getTotalLevel() < chanceAmount/100) {
                        stack.setCount(1);
                    }
                }
            }
        }
        return stack;
    }

    public static class Serializer extends LootFunction.Serializer<ApplyRequiredSkill>
    {
        public Serializer(ResourceLocation location)
        {
            super(location, ApplyRequiredSkill.class);
        }

        @Override
        public void serialize(JsonObject object, ApplyRequiredSkill functionClazz, JsonSerializationContext serializationContext)
        {
            object.add("skill", serializationContext.serialize(functionClazz.skillType.getName()));
            object.add("chance", serializationContext.serialize(functionClazz.chanceAmount));
        }

        @Override
        @Nonnull
        public ApplyRequiredSkill deserialize(JsonObject object, JsonDeserializationContext deserializationContext, LootCondition[] conditionsIn)
        {
            String skillName = JsonUtils.getString(object, "skill");
            SkillType<? extends SimpleSkill> skillType = SkillType.get(skillName, SimpleSkill.class);
            if (skillType == null)
            {
                throw new JsonParseException("Unknown skill type: '" + skillName + "'");
            }
            int tierIndex = JsonUtils.getInt(object, "tier");
            float amount = JsonUtils.getFloat(object, "chance");
            return new ApplyRequiredSkill(conditionsIn, skillType, SkillTier.valueOf(tierIndex), amount);
        }
    }
}
