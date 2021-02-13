/*
 * (C) Copyright 2020 The DKBans Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Philipp Elvin Friedhoff
 * @since 21.11.20, 17:11
 * @web %web%
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

package net.pretronic.dkbans.common.broadcast;

import net.pretronic.dkbans.api.broadcast.Broadcast;
import net.pretronic.dkbans.api.broadcast.BroadcastVisibility;
import net.pretronic.dkbans.common.DefaultDKBans;
import net.pretronic.libraries.document.Document;
import net.pretronic.libraries.document.type.DocumentFileType;

public class DefaultBroadcast implements Broadcast {

    private final int id;
    private String name;
    private BroadcastVisibility visibility;
    private final Document properties;

    public DefaultBroadcast(int id, String name, BroadcastVisibility visibility, Document properties) {
        this.id = id;
        this.name = name;
        this.visibility = visibility;
        this.properties = properties;
    }

    @Override
    public int getId() {
        return this.id;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
        DefaultDKBans.getInstance().getStorage().getBroadcast().update()
                .set("Name", name)
                .where("Id", getId())
                .execute();
    }

    @Override
    public BroadcastVisibility getVisibility() {
        return this.visibility;
    }

    @Override
    public void setVisibility(BroadcastVisibility visibility) {
        this.visibility = visibility;
        DefaultDKBans.getInstance().getStorage().getBroadcast().update()
                .set("Visibility", visibility)
                .where("Id", getId())
                .execute();
    }

    @Override
    public Document getProperties() {
        return this.properties;
    }

    @Override
    public void updateProperties() {
        DefaultDKBans.getInstance().getStorage().getBroadcast().update()
                .set("Properties", DocumentFileType.JSON.getWriter().write(Document.newDocument(this.properties), true))
                .where("Id", getId())
                .execute();
    }
}
