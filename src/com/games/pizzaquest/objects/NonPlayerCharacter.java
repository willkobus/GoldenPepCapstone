package com.games.pizzaquest.objects;

import java.util.HashMap;

public class NonPlayerCharacter implements PlayerInterface{
 private String name="";
 Boolean isQuestActive= false;
 private String npcLocation="";


 public String getNpcLocation() {
  return npcLocation;
 }

 public void setNpcLocation(String npcLocation) {
  this.npcLocation = npcLocation;
 }

 public String getNpcDescription() {
  return npcDescription;
 }

 public void setNpcDescription(String npcDescription) {
  this.npcDescription = npcDescription;
 }

 private String npcDescription= "";

 private HashMap<String, String> dialogue = new HashMap<String,String>();

 public NonPlayerCharacter(String name, String dialog, String npcLocation){
  setName(name);
  setDialogue(dialog);
  setNpcLocation(npcLocation);

 }


 public void setDialogue(String quest){
  dialogue.put("quest", quest);

 }

 public String giveQuest(){
  return dialogue.get("quest");
 }

 @Override
 public void setName(String name) {
  this.name=name;
 }

 @Override
 public String getName() {
  return name;
 }

 @Override
 public String toString() {
  return "NonPlayerCharacter{" +
          "name='" + name + '\'' +
          ", isQuestActive=" + isQuestActive +
          ", permanentLocation=" +
          '}';
 }
}