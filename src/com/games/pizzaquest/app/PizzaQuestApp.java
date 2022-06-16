package com.games.pizzaquest.app;
import com.games.pizzaquest.objects.*;
import com.games.pizzaquest.textparser.TextParser;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.Reader;

import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class PizzaQuestApp {

        //scanner for the game
        static Scanner scanner = new Scanner(System.in);
        //text parser for users to use
        //path for some ascii art
        private static final String bannerFilePath = "resources/WelcomeSplash.txt";
        private static final String helpFilePath = "resources/Instructions.txt";

        //track turn may be moved to player
        private int turns = 0;
        static final int END_OF_TURNS=10;
        public final List<String> itemList = List.of("pizza_cutter", "prosciutto", "wine_glass", "lemons", "coin", "ancient_pizza_cookbook", "moped", "cannoli", "marble_sculpture", "espresso");

        //Initial State of the Player, inventory and starting location
        private final Set<Item> inventory = new HashSet<>();
        public  Gamestate gamestate =null;
        public final Player player = new Player(inventory);

        private final ArrayList<NonPlayerCharacter> npcList= new ArrayList<NonPlayerCharacter>();


        //keep the game running until win/lose condition is met
        private boolean isGameOver = false;

        private Hashtable<String, Location> gameMap;
        private List<Location> locationList;
        private Type locationListType = new TypeToken<ArrayList<Location>>(){}.getType();


        public void execute() {
                TextParser parser = new TextParser();
                setGameOver(false);
                NpcGson();
                locationList = getLocationListFromJson();
                gameMap = hashNewMap(locationList);
                setNPC();
                welcome();
                gamestate = new Gamestate(gameMap.get("naples"));
                System.out.println(enterName());
                while(turns < END_OF_TURNS) {
                        //send user input to parser to validate and return a List
                        //then runs logic in relation to the map, and list based on Noun Verb Relationship

                        processCommands(parser.parse(scanner.nextLine()));
                 

                }
        }
        private void welcome() {
                try {
                        String text = Files.readString(Path.of(bannerFilePath));
                        System.out.println(text);
                } catch (IOException e) {
                        e.printStackTrace();
                }
        }

        private void gameInstructions() {
                try {
                        String text = Files.readString(Path.of(helpFilePath));
                        System.out.println(text);
                } catch (IOException e) {
                        e.printStackTrace();
                }

        }

        private String enterName() {
                System.out.println("Please enter your name: ");
                String playerName = scanner.nextLine();
                return ("Ciao " + playerName+ " you are in " + gamestate.getPlayerLocation());
        }

        private void quitGame() {
                System.out.println("You'll always have a pizza our heart ... Goodbye!");
                setGameOver(true);
                System.exit(0);
        }
        public boolean isGameOver() {
                return isGameOver;
        }

        public void setGameOver(boolean gameOver) {
                isGameOver = gameOver;
        }

        private void resetGame() {
                setGameOver(true);
                turns = 0;
                execute();
        }

        //take the processed command and the delegates this to another
        private void processCommands(List<String> verbAndNounList){
                String noun = verbAndNounList.get(verbAndNounList.size()-1);
                String verb = verbAndNounList.get(0);

                switch (verb) {
                        case "quit":
                                quitGame();
                                break;
                        case "go":
                                if (noun.equals("")){
                                        break;
                                }
                                String nextLoc = gamestate.getPlayerLocation().getNextLocation(noun);
                                System.out.println();
                                if(!nextLoc.equals("nothing")){
                                        System.out.println(nextLoc);
                                        gamestate.setPlayerLocation(gameMap.get(nextLoc.toLowerCase()));
                                        System.out.println();
                                        System.out.println(player.look(gamestate.getPlayerLocation()));
                                        System.out.println();
                                        turns++;
                                }
                                else{
                                        System.out.println("There is nothing that way!");
                                }
                                break;
                        case "look":
                                //look(); //player location or item  description printed
                                //will need a item list and a location list
                                //todo - check size and get last
                                //if room, do the first, else if item, do the second
                                if (noun.equals("")){
                                        break;
                                }
                                if(itemList.contains(noun)){
                                        System.out.println(player.look(new Item(noun)));
                                }else if (gamestate.getPlayerLocation().npc!= null && gamestate.getPlayerLocation().npc.getName().equals(noun)){
                                        System.out.println(gamestate.getPlayerLocation().npc.getNpcDescription());
                        }
                                else{
                                        System.out.println(player.look(gamestate.getPlayerLocation()));
                                }
                                break;
                        case "take":
                                //add item to inventory
                                player.addToInventory(noun);
                                break;
                        case "talk":
                                //add item to inventory
                                talk(noun);
                                break;
                        case "give":
                                //removes item from inventory
                                if (noun.equals("")){
                                        break;
                                }
                                player.removeFromInventory(noun);
                                break;
                        case "inventory":
                                Set<Item> tempInventory = player.getInventory();
                                System.out.println("Items in the Inventory");
                                for (Item item : tempInventory) {
                                        System.out.println(item.getName());
                                }
                                break;
                        case "help":
                                gameInstructions();
                                break;
                        case "reset":
                                resetGame();
                                break;
                        default:
                                System.out.printf("I don't understand '%s'%n", verbAndNounList);
                                System.out.println("Type help if you need some guidance on command structure!");
                                break;
                }
        }

        private void talk(String noun) {
                Location playerLocation = gamestate.getPlayerLocation();
                if(playerLocation.npc != null && playerLocation.npc.getName().equals(noun)){
                        System.out.println(playerLocation.npc.giveQuest());
                }else{
                        System.out.println("That player many not be in in this room or even exist!");
                }
        }

        public void NpcGson(){
                try {
                        // create Gson instance
                        Gson gson = new Gson();

                        // create a reader
                        Reader reader = Files.newBufferedReader(Paths.get("resources/npc.json"));

                        // convert JSON file to map
                        Map<String, ArrayList<String>> map = gson.fromJson(reader, Map.class);

                        // print map entries
                        for (Map.Entry<String, ArrayList<String>> entry : map.entrySet()) {
                                ArrayList<String> temp = map.get(entry.getKey());
                                NonPlayerCharacter npc = new NonPlayerCharacter(entry.getKey(),temp.get(0),temp.get(1));
                                npc.setNpcDescription(temp.get(2));
                                npcList.add(npc);
                        }

                        // close reader
                        reader.close();

                } catch (Exception ex) {
                        ex.printStackTrace();
                }
        }
        public List<Location> getLocationListFromJson(){
                ArrayList<Location> locationList = new ArrayList<>();
                try{
                        Gson gson = new Gson();
                        Reader reader = Files.newBufferedReader(Paths.get("resources/gamemap.json"));
                        locationList = gson.fromJson(reader, locationListType);
                        reader.close();

                }
                catch(Exception e){
                        e.printStackTrace();
                }
                return locationList;
        }

        public Hashtable<String, Location> hashNewMap(List<Location> initialMap) {
                Hashtable<String, Location> newMap = new Hashtable<>();
                for(Location location: initialMap){
                        newMap.put(location.getName(), location);
                }
                return newMap;
        }

        public void setNPC(){
                String tempNPCLocation = "";
                Location setNPCLocation= null;
                for (NonPlayerCharacter person:npcList
                     ) {
                        tempNPCLocation= person.getNpcLocation();
                        setNPCLocation= gameMap.get(tempNPCLocation);
                        if(setNPCLocation != null){
                        setNPCLocation.setNpc(person);}
                }

        }
}