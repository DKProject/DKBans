/*
 * (C) Copyright 2018 The DKBans Project (Davide Wietlisbach)
 *
 * @author Davide Wietlisbach
 * @since 30.12.18 14:39
 * @Website https://github.com/DevKrieger/DKBans
 *
 * The DKBans Project is under the Apache License, version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package ch.dkrieger.bansystem.lib.config;

import ch.dkrieger.bansystem.lib.Messages;
import ch.dkrieger.bansystem.lib.utils.GeneralUtil;
import net.pretronic.libraries.document.Document;
import net.pretronic.libraries.document.type.DocumentFileType;

import java.io.File;

public abstract class SimpleConfig {

    private transient final File file;
    private transient Document config;

    public SimpleConfig(File file) {
        this.file = file;

        try{
            file.getParentFile().mkdirs();
            if(!file.exists()) this.file.createNewFile();
        }catch (Exception exception){
            System.out.println(Messages.SYSTEM_PREFIX+"Could not create config file.");
            System.out.println(Messages.SYSTEM_PREFIX+"Error: "+exception.getMessage());
        }
    }
    public File getFile() {
        return file;
    }
    public void reloadConfig(){
        loadConfig();
    }
    public void loadConfig(){
        load();
        onLoad();
        save();
    }
    public void save(){

    }
    public void load(){
        if(file == null) return;
        try{
            if(file.exists()) file.createNewFile();
            this.config = DocumentFileType.YAML.getReader().read(file);
        }catch (Exception exception){
            System.out.println(Messages.SYSTEM_PREFIX+"Could not load config file.");
            System.out.println(Messages.SYSTEM_PREFIX+"Error: "+exception.getMessage());
        }
    }
    public void addValue(String path, Object value){
        if(!this.config.contains(path))this.config.set(path,value);
    }
    public String getStringValue(String path){
        return this.config.getString(path);
    }


    /*
    public Object getValue(String path){
        return this.config.getStringList(path);
    }

     */
    public String addAndGetStringValue(String path, Object object){
        addValue(path,object);
        return this.config.getString(path);
    }
    public String addAndGetMessageValue(String path,Object object){
        addValue(path,object);
        String result = getStringValue(path);
        if(result == null) return "";
        return GeneralUtil.buildNextLineColor(result);
    }
    public int addAndGetIntValue(String path,Object object){
        addValue(path,object);
        return this.config.getInt(path);
    }
    public double addAndGetDoubleValue(String path,Object object){
        addValue(path,object);
        return this.config.getDouble(path);
    }
    public long addAndGetLongValue(String path,Object object){
        addValue(path,object);
        return this.config.getLong(path);
    }
    public boolean addAndGetBooleanValue(String path,Object object){
        addValue(path,object);
        return this.config.getBoolean(path);
    }

    public char addAndGetCharValue(String path,Object object){
        addValue(path,object);
        return this.config.getCharacter(path);
    }

    public Boolean contains(String path){
        return this.config.contains(path);
    }
    public abstract void onLoad();
}
