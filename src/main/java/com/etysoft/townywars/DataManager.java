package com.etysoft.townywars;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Town;
import org.bukkit.Bukkit;

import java.io.*;
import java.util.HashSet;
import java.util.Set;

public class DataManager {

    public static void saveNeutrals(Set<Town> neutralslist) {
        Bukkit.getConsoleSender().sendMessage("Saving neutrals.dat...");
        File file = new File(TownyWars.instance.getDataFolder(), "neutrals.dat");
        try (BufferedWriter bf = new BufferedWriter(new FileWriter(file))) {
            //iterate map entries
            for (Town t : neutralslist) {
                //put key and value separated by a colon
                bf.write(t.getName());
                //new line
                bf.newLine();
            }

            bf.flush();
        } catch (Exception e) {
            e.printStackTrace();
            Bukkit.getConsoleSender().sendMessage("ERROR SAVING NEUTRAL TOWNS!");
        }
        //always close the writer
    }

    public static Set<Town> loadNeutrals() {
        Bukkit.getConsoleSender().sendMessage("Loading neutrals file...");
        Set<Town> neutralslist = new HashSet<>();
        try {
            File file = new File(TownyWars.instance.getDataFolder(), "neutrals.dat");
            //создаем объект FileReader для объекта File
            FileReader fr = new FileReader(file);
            //создаем BufferedReader с существующего FileReader для построчного считывания
            BufferedReader reader = new BufferedReader(fr);
            // считаем сначала первую строку
            String line = reader.readLine();
            while (line != null) {
                 try {
                     neutralslist.add(TownyAPI.getInstance().getDataSource().getTown(line));
                 } catch (NotRegisteredException ex) {
                     Bukkit.getConsoleSender().sendMessage("Town with name " + line + " not found!");
                 }

                // считываем остальные строки в цикле
                line = reader.readLine();
            }
        } catch (FileNotFoundException e) {
            Bukkit.getConsoleSender().sendMessage("Neutrals list not found.");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return neutralslist;
    }

}
