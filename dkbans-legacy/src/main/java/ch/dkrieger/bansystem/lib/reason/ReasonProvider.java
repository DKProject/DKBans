/*
 * (C) Copyright 2019 The DKBans Project (Davide Wietlisbach)
 *
 * @author Davide Wietlisbach
 * @since 14.03.19 19:43
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

package ch.dkrieger.bansystem.lib.reason;

import ch.dkrieger.bansystem.lib.DKBansPlatform;
import ch.dkrieger.bansystem.lib.Messages;
import ch.dkrieger.bansystem.lib.player.history.BanType;
import ch.dkrieger.bansystem.lib.player.history.HistoryPoints;
import ch.dkrieger.bansystem.lib.utils.Duration;
import ch.dkrieger.bansystem.lib.utils.GeneralUtil;
import net.pretronic.libraries.document.Document;
import net.pretronic.libraries.document.type.DocumentFileType;
import net.pretronic.libraries.utility.reflect.TypeReference;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class ReasonProvider {

    private DKBansPlatform platform;
    private List<KickReason> kickReasons;
    private List<BanReason> banReasons;
    private List<ReportReason> reportReasons;
    private List<UnbanReason> unbanReasons;
    private List<WarnReason> warnReasons;

    public ReasonProvider(DKBansPlatform platform){
        this.platform = platform;
        this.banReasons = new ArrayList<>();
        this.kickReasons = new ArrayList<>();
        this.reportReasons = new ArrayList<>();
        this.unbanReasons = new ArrayList<>();
        this.warnReasons = new ArrayList<>();
        loadBanReasons();
        loadUnbanReasons();
        loadReportReasons();
        loadKickReasons();
        loadWarnReasons();
    }
    public ReasonProvider(DKBansPlatform platform,List<KickReason> kickReasons, List<BanReason> banReasons, List<ReportReason> reportReasons, List<UnbanReason> unbanReasons) {
        this.platform = platform;
        this.kickReasons = kickReasons;
        this.banReasons = banReasons;
        this.reportReasons = reportReasons;
        this.unbanReasons = unbanReasons;
    }
    public List<KickReason> getKickReasons() {
        return kickReasons;
    }
    public List<BanReason> getBanReasons() {
        return banReasons;
    }
    public List<ReportReason> getReportReasons() {
        return reportReasons;
    }
    public List<UnbanReason> getUnbanReasons() {
        return unbanReasons;
    }
    public List<WarnReason> getWarnReasons() {
        return warnReasons;
    }

    public KickReason searchKickReason(String search){
        if(GeneralUtil.isNumber(search)) return getKickReason(Integer.valueOf(search));
        return getKickReason(search);
    }
    public KickReason getKickReason(int id){
      return GeneralUtil.iterateOne(this.kickReasons,reason -> reason.getID() == id);
    }
    public KickReason getKickReason(String name){
        return GeneralUtil.iterateOne(this.kickReasons,reason -> reason.hasAlias(name));
    }

    public BanReason searchBanReason(String search){
        if(GeneralUtil.isNumber(search)) return getBanReason(Integer.valueOf(search));
        return getBanReason(search);
    }

    public BanReason getBanReason(int id){
        return GeneralUtil.iterateOne(this.banReasons,reason -> reason.getID() == id);
    }


    public BanReason getBanReason(String name){
        return GeneralUtil.iterateOne(this.banReasons,reason -> reason.hasAlias(name));
    }

    public ReportReason searchReportReason(String search){
        if(GeneralUtil.isNumber(search)) return getReportReason(Integer.valueOf(search));
        return getReportReason(search);
    }

    public ReportReason getReportReason(int id){
        return GeneralUtil.iterateOne(this.reportReasons,reason -> reason.getID() == id);
    }
    public ReportReason getReportReason(String name){
        return GeneralUtil.iterateOne(this.reportReasons,reason -> reason.hasAlias(name));
    }

    public UnbanReason searchUnbanReason(String search){
        if(GeneralUtil.isNumber(search)) return getUnbanReason(Integer.valueOf(search));
        return getUnbanReason(search);
    }
    public UnbanReason getUnbanReason(int id){
        return GeneralUtil.iterateOne(this.unbanReasons,reason -> reason.getID() == id);
    }
    public UnbanReason getUnbanReason(String name){
        return GeneralUtil.iterateOne(this.unbanReasons,reason -> reason.hasAlias(name));
    }

    public WarnReason searchWarnReason(String search){
        if(GeneralUtil.isNumber(search)) return getWarnReason(Integer.valueOf(search));
        return getWarnReason(search);
    }
    public WarnReason getWarnReason(int id){
        return GeneralUtil.iterateOne(this.warnReasons,reason -> reason.getID() == id);
    }
    public WarnReason getWarnReason(String name){
        return GeneralUtil.iterateOne(this.warnReasons,reason -> reason.hasAlias(name));
    }

    public void loadBanReasons(){
        File file = new File(this.platform.getFolder(),"ban-reasons.yml");
        net.pretronic.libraries.document.Document config = null;
        if(file.exists()){
            try{
                config = DocumentFileType.YAML.getReader().read(file);
                Document reasons = config.getDocument("reasons");
                this.banReasons = new ArrayList<>();
                if(reasons != null){
                    for(String key : reasons.keys()){
                        try{
                            if(!config.contains("reasons."+key+".points.durations")){
                                config.set("reasons."+key+".points.durations.0.type",BanType.NETWORK.toString());
                                config.set("reasons."+key+".points.durations.0.time",-2);
                                config.set("reasons."+key+".points.durations.0.unit",TimeUnit.DAYS.toString());
                                //YamlConfiguration.getProvider(YamlConfiguration.class).save(config,file);
                            }

                            Map<Integer,BanReasonEntry> templateDurations = new HashMap<>();
                            Document durationConfig = config.getDocument("reasons."+key+".durations");
                            for(String DKey : durationConfig.keys()){
                                templateDurations.put(Integer.valueOf(DKey),new BanReasonEntry(
                                        BanType.valueOf(config.getString("reasons."+key+".durations."+DKey+".type"))
                                        ,config.getLong("reasons."+key+".durations."+DKey+".time")
                                        ,TimeUnit.valueOf(config.getString("reasons."+key+".durations."+DKey+".unit"))));
                            }
                            Map<Integer,BanReasonEntry> pointsDurations = new HashMap<>();
                            Document pointsConfig = config.getDocument("reasons."+key+".points.durations");
                            for(String DKey : pointsConfig.keys()){
                                pointsDurations.put(Integer.valueOf(DKey),new BanReasonEntry(
                                        BanType.valueOf(config.getString("reasons."+key+".points.durations."+DKey+".type"))
                                        ,config.getLong("reasons."+key+".points.durations."+DKey+".time")
                                        ,TimeUnit.valueOf(config.getString("reasons."+key+".points.durations."+DKey+".unit"))));
                            }
                            this.banReasons.add(new BanReason(Integer.parseInt(key)
                                    ,new HistoryPoints(config.getInt("reasons."+key+".points.points")
                                    ,BanType.valueOf(config.getString("reasons."+key+".historytype")))
                                    ,config.getString("reasons."+key+".name")
                                    ,config.getString("reasons."+key+".display")
                                    ,config.getString("reasons."+key+".permission")
                                    ,config.getBoolean("reasons."+key+".hidden")
                                    ,config.getObject("reasons."+key+".points.aliases",new TypeReference<List<String>>(){}.getType())
                                    ,new ch.dkrieger.bansystem.lib.utils.Document()
                                    ,config.getDouble("reasons."+key+".points.divider")
                                    ,BanType.valueOf(config.getString("reasons."+key+".historytype"))
                                    ,templateDurations,pointsDurations));
                        }catch (Exception exception){
                            exception.printStackTrace();
                            System.out.println(Messages.SYSTEM_PREFIX+"Could not load ban-reason "+key);
                            System.out.println(Messages.SYSTEM_PREFIX+"Error: "+exception.getMessage());
                        }
                    }
                }
                if(this.banReasons.size() > 0) return;
            }catch (Exception exception){
                if(file.exists()){
                    file.renameTo(new File(this.platform.getFolder(),"ban-reasons-old-"+GeneralUtil.getRandomString(15)+".yml"));
                    System.out.println(Messages.SYSTEM_PREFIX+"Could not load ban-reasons, generating new (Saved als old)");
                    System.out.println(Messages.SYSTEM_PREFIX+"Error: "+exception.getMessage());
                }
            }
        }
        config = Document.newDocument();
    }
    public void loadUnbanReasons(){
        File file = new File(this.platform.getFolder(),"unban-reasons.yml");
        Document config = null;
        if(file.exists()){
            try{
                config = DocumentFileType.YAML.getReader().read(file);
                Document reasons =config.getDocument("reasons");
                this.unbanReasons = new ArrayList<>();
                if(reasons != null){
                    for(String key : reasons.keys()){
                        try{
                            this.unbanReasons.add(new UnbanReason(Integer.parseInt(key)
                                    ,new HistoryPoints(config.getInt("reasons."+key+".points.remove.points"),BanType.NETWORK)
                                    ,config.getString("reasons."+key+".name")
                                    ,config.getString("reasons."+key+".display")
                                    ,config.getString("reasons."+key+".permission")
                                    ,config.getBoolean("reasons."+key+".hidden")
                                    ,config.getObject("reasons."+key+".aliases",new TypeReference<List<String>>(){}.getType())
                                    ,new ch.dkrieger.bansystem.lib.utils.Document()
                                    ,config.getInt("reasons."+key+".points.maximal")
                                    ,config.getBoolean("reasons."+key+".remove.all")
                                    ,config.getObject("reasons."+key+".notforbanid",new TypeReference<List<Integer>>(){}.getType())
                                    ,new Duration(config.getLong("reasons."+key+".duration.maximal.time")
                                    ,TimeUnit.valueOf(config.getString("reasons."+key+".duration.maximal.unit")))
                                    ,new Duration(config.getLong("reasons."+key+".duration.remove.time")
                                    ,TimeUnit.valueOf(config.getString("reasons."+key+".duration.remove.unit")))
                                    ,config.getDouble("reasons."+key+".duration.divider")
                                    ,config.getDouble("reasons."+key+".points.divider")
                                    ,BanType.parseNull(config.getString("reasons."+key+".bantype"))));
                        }catch (Exception exception){
                            exception.printStackTrace();
                            System.out.println(Messages.SYSTEM_PREFIX+"Could not load unban-reason "+key);
                            System.out.println(Messages.SYSTEM_PREFIX+"Error: "+exception.getMessage());
                        }
                    }
                }
                if(this.unbanReasons.size() > 0) return;
            }catch (Exception exception){
                if(file.exists()){
                    file.renameTo(new File(this.platform.getFolder(),"unban-reasons-old-"+GeneralUtil.getRandomString(15)+".yml"));
                    System.out.println(Messages.SYSTEM_PREFIX+"Could not load unban-reasons, generating new (Saved als old)");
                }
            }
        } //int id, int points, String name, String display, String permission, boolean hidden, List<String> aliases, int maxPoints, boolean removeAllPoints, List<Integer> notForBanID, Duration maxDuration, Duration removeDuration, double durationDivider
        config = Document.newDocument();
    }
    public void loadReportReasons(){
        File file = new File(this.platform.getFolder(),"report-reasons.yml");
        Document config = null;
        if(file.exists()){
            try{
                config = DocumentFileType.YAML.getReader().read(file);
                Document reasons = config.getDocument("reasons");
                this.reportReasons = new ArrayList<>();
                if(reasons != null) {
                    for (String key : reasons.keys()) {
                        try{
                            this.reportReasons.add(new ReportReason(Integer.parseInt(key)
                                    ,config.getString("reasons."+key+".name")
                                    ,config.getString("reasons."+key+".display")
                                    ,config.getString("reasons."+key+".permission")
                                    ,config.getBoolean("reasons."+key+".hidden")
                                    ,config.getObject("reasons."+key+".aliases",new TypeReference<List<String>>(){}.getType())
                                    ,new ch.dkrieger.bansystem.lib.utils.Document()
                                    ,config.getInt("reasons."+key+".forbanreason")));
                        }catch (Exception exception){
                            exception.printStackTrace();
                            System.out.println(Messages.SYSTEM_PREFIX+"Could not load report-reason "+key);
                            System.out.println(Messages.SYSTEM_PREFIX+"Error: "+exception.getMessage());
                        }
                    }
                }
                if(this.reportReasons.size() > 0) return;
            }catch (Exception exception){
                if(file.exists()){
                    file.renameTo(new File(this.platform.getFolder(),"report-reasons-old-"+GeneralUtil.getRandomString(15)+".yml"));
                    System.out.println(Messages.SYSTEM_PREFIX+"Could not load report-reasons, generating new (Saved als old)");
                    System.out.println(Messages.SYSTEM_PREFIX+"Error: "+exception.getMessage());
                }
            }
        }
        config = Document.newDocument();
    }
    public void loadKickReasons(){
        File file = new File(this.platform.getFolder(),"kick-reasons.yml");
        Document config = null;
        if(file.exists()){
            try{
                config = DocumentFileType.YAML.getReader().read(file);
                Document reasons = config.getDocument("reasons");
                this.kickReasons= new ArrayList<>();
                if(reasons != null) {
                    for(String key : reasons.keys()) {
                        try{
                            this.kickReasons.add(new KickReason(Integer.parseInt(key)
                                    ,new HistoryPoints(config.getInt("reasons."+key+".points.points")
                                    ,BanType.valueOf(config.getString("reasons."+key+".points.type")))
                                    ,config.getString("reasons."+key+".name")
                                    ,config.getString("reasons."+key+".display")
                                    ,config.getString("reasons."+key+".permission")
                                    ,config.getBoolean("reasons."+key+".hidden")
                                    ,config.getObject("reasons."+key+".aliases",new TypeReference<List<String>>(){}.getType())
                                    ,new ch.dkrieger.bansystem.lib.utils.Document()));
                        }catch (Exception exception){
                            exception.printStackTrace();
                            System.out.println(Messages.SYSTEM_PREFIX+"Could not load kick-reason "+key);
                            System.out.println(Messages.SYSTEM_PREFIX+"Error: "+exception.getMessage());
                        }
                    }
                }
                if(this.kickReasons.size() > 0) return;
            }catch (Exception exception){
                if(file.exists()){
                    file.renameTo(new File(this.platform.getFolder(),"kick-reasons-old-"+GeneralUtil.getRandomString(15)+".yml"));
                    System.out.println(Messages.SYSTEM_PREFIX+"Could not load kick-reasons, generating new (Saved als old)");
                    exception.printStackTrace();
                }
            }
        }
        config =  Document.newDocument();
    }
    public void loadWarnReasons(){
        File file = new File(this.platform.getFolder(),"warn-reasons.yml");
        Document config = null;
        if(file.exists()){
            try{
                config = DocumentFileType.YAML.getReader().read(file);
                Document reasons = config.getDocument("reasons");
                this.warnReasons= new ArrayList<>();
                if(reasons != null) {
                    for (String key : reasons.keys()) {
                        try{
                            this.warnReasons.add(new WarnReason(Integer.valueOf(key)
                                    ,new HistoryPoints(config.getInt("reasons."+key+".points.points")
                                    ,BanType.valueOf(config.getString("reasons."+key+".points.type")))
                                    ,config.getString("reasons."+key+".name")
                                    ,config.getString("reasons."+key+".display")
                                    ,config.getString("reasons."+key+".permission")
                                    ,config.getBoolean("reasons."+key+".hidden")
                                    ,config.getObject("reasons."+key+".aliases",new TypeReference<List<String>>(){}.getType())
                                    ,new ch.dkrieger.bansystem.lib.utils.Document()
                                    ,config.getInt("reasons."+key+".autoban.count")
                                    ,config.getInt("reasons."+key+".autoban.banid"),config.getInt("reasons."+key+".kickcount")));
                        }catch (Exception exception){
                            exception.printStackTrace();
                            System.out.println(Messages.SYSTEM_PREFIX+"Could not load warn-reason "+key);
                            System.out.println(Messages.SYSTEM_PREFIX+"Error: "+exception.getMessage());
                        }
                    }
                }
                if(this.warnReasons.size() > 0) return;
            }catch (Exception exception){
                if(file.exists()){
                    file.renameTo(new File(this.platform.getFolder(),"warn-reasons-old-"+GeneralUtil.getRandomString(15)+".yml"));
                    System.out.println(Messages.SYSTEM_PREFIX+"Could not load warn-reasons, generating new (Saved als old)");
                }
            }
        }
        config = Document.newDocument();
    }
}
