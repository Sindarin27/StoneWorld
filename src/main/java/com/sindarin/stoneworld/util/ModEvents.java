package com.sindarin.stoneworld.util;

import com.sindarin.stoneworld.StoneWorld;
import com.sindarin.stoneworld.items.ModItems;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ObjectHolder;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@ObjectHolder(StoneWorld.MOD_ID)
@Mod.EventBusSubscriber
public class ModEvents {
    //Time units and their corresponding amount of seconds (in real time)
    private static Map<String, Float> TimeUnits = new HashMap<>();
    private static Map<String, Float> DistanceUnits = new HashMap<>();
    //Initialise units
    static {
        //Time units: just the most standard ones
        TimeUnits.put("second", 1f);
        TimeUnits.put("seconds", 1f);
        TimeUnits.put("minute", 60f);
        TimeUnits.put("minutes", 60f);
        TimeUnits.put("hour", 3600f);
        TimeUnits.put("hours", 3600f);

        DistanceUnits.put("meter", 1f);
        DistanceUnits.put("meters", 1f);
        DistanceUnits.put("kilometer", 1000f);
        DistanceUnits.put("kilometers", 1000f);
        DistanceUnits.put("block", 1f);
        DistanceUnits.put("blocks", 1f);
    }

    @SubscribeEvent
    public static void onChat(ServerChatEvent event) {
        String message = event.getMessage();
        ItemStack heldItemstack = event.getPlayer().getHeldItemMainhand();

        if (heldItemstack.getItem() == ModItems.medusa) {
            tryMedusa(message, heldItemstack);
        }
    }

    //Check whether a set of unlocalised strings contains a certain message
    private static boolean containsOneOfString(Set<String> strings, String messageToCheck) {
        for (String string:strings) {
            if(messageToCheck.contains(string)) return true;
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
            float distance;
            float time;

            //First part might not be a number, so try it and if not, cancel checking to manage
            try { distance = Integer.parseInt(splitMessage[0]); } catch (NumberFormatException NAN){ return; }
            //Multiply according to the distance unit supplied
            distance = distance * DistanceUnits.getOrDefault(splitMessage[1], 0f);
            if (distance <= 0) return;

            //Third part might not be a number, so try it and if not, cancel checking to manage
            try { time = Integer.parseInt(splitMessage[2]); } catch (NumberFormatException NAN){ return; }
            //Multiply according to the time unit supplied
            time = time * TimeUnits.getOrDefault(splitMessage[3], 0f);
            if (time <= 0) return;

            //Make a nbt compound with the specified distance and distance
            CompoundNBT compound = itemStack.getOrCreateTag();
            compound.putFloat("distance", distance);
            compound.putFloat("time", time);

            //Write the nbt to the item
            itemStack.setTag(compound);
        }
    }
}
