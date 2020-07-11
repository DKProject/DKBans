/*
 * (C) Copyright 2020 The DKBans Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Philipp Elvin Friedhoff
 * @since 05.07.20, 19:51
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

package ch.dkrieger.bansystem.lib.broadcast;

public class Broadcast {

    private int id;
    private String message, hover, permission;
    private long created, lastChange;
    private boolean auto;
    private Click click;

    public Broadcast(int id, String message, String permission, String hover, long created, long lastChange, boolean auto, Click click) {
        this.id = id;
        this.message = message;
        this.permission = permission;
        this.hover = hover;
        this.created = created;
        this.lastChange = lastChange;
        this.auto = auto;
        this.click = click;
    }

    public int getID() {
        return id;
    }

    public String getMessage() {
        return "";
    }

    public String getPermission() {
        return "";
    }

    public String getHover() {
        return "";
    }

    public long getCreated() {
        return created;
    }
    public long getLastChange() {
        return lastChange;
    }
    public boolean isAuto() {
        return auto;
    }

    public Click getClick() {
        return click;
    }

    public void setID(int id) {
        this.id = id;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setHover(String hover) {
        this.hover = hover;
    }

    public void setCreated(long created) {
        this.created = created;
    }

    public void setLastChange(long lastChange) {
        this.lastChange = lastChange;
    }

    public void setAuto(boolean auto) {
        this.auto = auto;
    }

    public void setClick(Click click) {
        this.click = click;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }


    public static class Click {

        private String message;
        private ClickType type;

        public Click(String message, ClickType type) {
            this.message = message;
            this.type = type;
        }
        public String getMessage() {
            return message;
        }
        public ClickType getType() {
            return type;
        }

        public void setMessage(String message) {
            this.message = message;
        }
        public void setType(ClickType type) {
            this.type = type;
        }

    }
    public static enum ClickType {
        URL(),
        COMMAND(),
        SERVER(),
        SERVERGROUP(),
        OPENCHAT();

        public static ClickType parseNull(String parse){
            try{
                return valueOf(parse);
            }catch (Exception exception){}
            return null;
        }

    }
}
