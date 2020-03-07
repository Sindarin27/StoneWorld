package com.sindarin.stoneworld.util;

import com.sindarin.stoneworld.StoneWorld;
import com.sindarin.stoneworld.entities.DroppedMedusaEntity;
import com.sindarin.stoneworld.items.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTTypes;
import net.minecraftforge.client.event.ClientChatEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ObjectHolder;
import sun.security.ssl.Debug;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@ObjectHolder(StoneWorld.MOD_ID)
@Mod.EventBusSubscriber
public class ModEvents {
    //Time units and their corresponding amount of seconds (in real time)
    public static Map<String, Float> TimeUnits = new HashMap<String, Float>();
    public static Map<String, Float> DistanceUnits = new HashMap<String, Float>();
    //Initialise units
    static {
        //Time units: just the most standard ones
        TimeUnits.put("units.stoneworld.second", 1f);
        TimeUnits.put("units.stoneworld.seconds", 1f);
        TimeUnits.put("units.stoneworld.minute", 60f);
        TimeUnits.put("units.stoneworld.minutes", 60f);
        TimeUnits.put("units.stoneworld.hour", 3600f);
        TimeUnits.put("units.stoneworld.hours", 3600f);

        DistanceUnits.put("units.stoneworld.meter", 1f);
        DistanceUnits.put("units.stoneworld.meters", 1f);
        DistanceUnits.put("units.stoneworld.kilometer", 1000f);
        DistanceUnits.put("units.stoneworld.kilometers", 1000f);
        DistanceUnits.put("units.stoneworld.block", 1f);
        DistanceUnits.put("units.stoneworld.blocks", 1f);
    }

    @SubscribeEvent
    public static void onChat(ServerChatEvent event) {
        System.out.println("Checking chat");
        String message = event.getMessage();
        ItemStack heldItemstack = event.getPlayer().getHeldItemMainhand();

        if (heldItemstack.getItem() == ModItems.medusa) {
            System.out.println("Attempting medusa activation");
            tryMedusa(message, heldItemstack);
        }
    }

    /*@SubscribeEvent
    public static void onEntityJoinsWorld(EntityJoinWorldEvent event) {
        //If the entity is an item and not already one of our converted items, convert it
        if (event.getEntity() instanceof ItemEntity && !(event.getEntity() instanceof DroppedMedusaEntity)) {
            ItemEntity itemEntity = (ItemEntity)event.getEntity();
            if (itemEntity.getItem().getItem() == ModItems.medusa) {
                System.out.println("Replacing item with medusa");
                ItemEntity newItemEntity = new DroppedMedusaEntity((EntityType<? extends ItemEntity>) itemEntity.getType(), event.getWorld());
                newItemEntity.setItem(itemEntity.getItem());
                newItemEntity.setOwnerId(itemEntity.getOwnerId());
                newItemEntity.setThrowerId(itemEntity.getThrowerId());
                newItemEntity.setMotion(itemEntity.getMotion());
                newItemEntity.setPosition(itemEntity.getPosition().getX(), itemEntity.getPosition().getY(), itemEntity.getPosition().getZ());
                newItemEntity.setDefaultPickupDelay();
                event.getWorld().addEntity(newItemEntity);
                itemEntity.remove();
            }
        }
    }*/

    //Check whether a set of unlocalised strings contains a certain message
    private static boolean containsOneOfString(Set<String> strings, String messageToCheck) {
        for (String string:strings) {
            if(messageToCheck.contains(I18n.format(string))) return true;
        }
        return false;
    }
    //Find which string in a set of unlocalised strings contains a certain message
    private static String findMatchingString(Set<String> strings, String messageToCheck) {
        for (String string:strings) {
            if(I18n.format(string).equals(messageToCheck)) return string;
        }
        return null;
    }

    //Try a medusa activation with the supplied text string
    private static void tryMedusa(String message, ItemStack itemStack) {
        //If our chat message looks like a trigger message
        if (Character.isDigit(message.charAt(0)) //Check if the first character of a message is a number (req. for acivation)
                && containsOneOfString(TimeUnits.keySet(), message) //If it also contains a time unit
                && containsOneOfString(DistanceUnits.keySet(), message) //And a distance unit
        ) {
            //Go analyze the message
            String[] splitMessage = message.split(" ");
            float distance = 0;
            float time = 0;

            //First part might not be a number, so try it and if not, cancel checking to manage
            try { distance = Integer.parseInt(splitMessage[0]); } catch (NumberFormatException NAN){ return; }
            //Multiply according to the distance unit supplied
            String distanceUnit = findMatchingString(DistanceUnits.keySet(), splitMessage[1]);
            if (distanceUnit == null) return;
            distance = distance * DistanceUnits.get(distanceUnit);

            //Third part might not be a number, so try it and if not, cancel checking to manage
            try { time = Integer.parseInt(splitMessage[2]); } catch (NumberFormatException NAN){ return; }
            //Multiply according to the time unit supplied
            String timeUnit = findMatchingString(TimeUnits.keySet(), splitMessage[3]);
            if (timeUnit == null) return;
            time = time * TimeUnits.get(timeUnit);

            //Make a nbt compound with the specified distance and distance
            CompoundNBT compound = itemStack.getOrCreateTag();
            compound.putFloat("distance", distance);
            compound.putFloat("time", time);

            //Write the nbt to the item
            itemStack.setTag(compound);
        }
    }
}
