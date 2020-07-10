/*
 * (C) Copyright 2020 The DKBans Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 10.07.20, 22:08
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

package net.pretronic.dkbans.common;

import net.pretronic.libraries.document.Document;
import net.pretronic.libraries.document.type.DocumentFileType;
import net.pretronic.libraries.utility.map.Pair;

import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;

public class DKBansUtil {

    public static Pair<String,String> lookupLocation(String ip){
        try{
            InputStream stream = new URL("http://ip-api.com/json/"+ip+"?fields=country,regionName").openStream();
            Scanner scanner = new Scanner(stream, "UTF-8").useDelimiter("\\A");
            Document document = DocumentFileType.JSON.getReader().read(scanner.next());
            if(!document.isEmpty()) {
                return new Pair<>(document.getString("country"),document.getString("regionName"));
            }
        }catch (Exception ignored){}
        return new Pair<>("Unknown","Unknown");
    }


}
