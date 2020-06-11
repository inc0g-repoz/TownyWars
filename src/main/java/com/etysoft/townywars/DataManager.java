package com.etysoft.townywars;

import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.TownyUniverse;
import org.bukkit.Bukkit;

import java.io.*;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class DataManager {

    public static void saveNeutrals(Set<Town> neutralslist)
    {
        Bukkit.getConsoleSender().sendMessage("Saving neutrals.dat...");
        File file = new File(TownyWars.instance.getDataFolder(), "neutrals.dat");

        BufferedWriter bf = null;;

        try{

            //create new BufferedWriter for the output file
            bf = new BufferedWriter( new FileWriter(file) );

            //iterate map entries
            for(Town t : neutralslist){

                //put key and value separated by a colon
                bf.write(t.getName());

                //new line
                bf.newLine();
            }

            bf.flush();

        }catch(Exception e){
            e.printStackTrace();
            Bukkit.getConsoleSender().sendMessage("ERROR SAVING NEUTRAL TOWNS!");
        }finally{

            try{
                //always close the writer
                bf.close();
            }catch(Exception e){}
        }
    }

    public static Set<Town> loadNeutrals()
    {
        Bukkit.getConsoleSender().sendMessage("Loading neutrals file...");
        Set<Town> neutralslist = new HashSet<Town>();
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

                         neutralslist.add(TownyUniverse.getDataSource().getTown(line));
                         Bukkit.getConsoleSender().sendMessage("Town with name " + line + " added!");
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
