/*
 * (C) Copyright 2020 The DKBans Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Philipp Elvin Friedhoff
 * @since 05.07.20, 18:46
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

package ch.dkrieger.bansystem.lib.player;

import ch.dkrieger.bansystem.lib.player.history.BanType;
import ch.dkrieger.bansystem.lib.player.history.History;
import ch.dkrieger.bansystem.lib.player.history.entry.Ban;
import ch.dkrieger.bansystem.lib.report.Report;
import ch.dkrieger.bansystem.lib.stats.PlayerStats;
import ch.dkrieger.bansystem.lib.utils.Document;
import ch.dkrieger.bansystem.lib.utils.GeneralUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class NetworkPlayer {

    private int id;
    private UUID uuid;
    private String name, color, lastIP, lastCountry;
    private long lastLogin, firstLogin, onlineTime;
    private boolean bypass, teamChatLogin, reportLogin;
    private History history;
    private Document properties;
    private PlayerStats stats;
    private List<OnlineSession> onlineSessions;
    private List<Report> reports;

    public NetworkPlayer(int id, UUID uuid, String name, String color, String lastIP, String lastCountry, long lastLogin, long firstLogin, long onlineTime, boolean bypass, boolean teamChatLogin, boolean reportLogin, History history, Document properties, PlayerStats stats, List<OnlineSession> onlineSessions, List<Report> reports) {
        this.id = id;
        this.uuid = uuid;
        this.name = name;
        this.color = color;
        this.lastIP = lastIP;
        this.lastCountry = lastCountry;
        this.lastLogin = lastLogin;
        this.firstLogin = firstLogin;
        this.onlineTime = onlineTime;
        this.bypass = bypass;
        this.teamChatLogin = teamChatLogin;
        this.reportLogin = reportLogin;
        this.history = history;
        this.properties = properties;
        this.stats = stats;
        this.onlineSessions = onlineSessions;
        this.reports = reports;
    }

    /**
     * Every player has a unique ide which is given by DKBans.
     *
     * @return The id of the player
     */
    public int getID() {
        return id;
    }

    /**
     * @return The minecraft player name
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @return The minecraft name an the color before the name
     */
    public String getColoredName(){
        return color+this.name;
    }

    /**
     * Get the player's last country.
     *
     * @return The last country from the player
     */
    public String getCountry(){
        return this.lastCountry;
    }

    /**
     * If the ip saving is disabled in the config, no ip will be available.
     *
     * @return The last ip from the player
     */
    public String getIP(){
        return this.lastIP;
    }

    /**
     *
     * @return The minecraft uuid from the player
     */
    public UUID getUUID(){
        return this.uuid;
    }

    /**
     *
     * @return The last login time from the player in milliseconds
     */
    public long getLastLogin() {
        return lastLogin;
    }

    /**
     *
     * @return The last login time from the player in milliseconds
     */
    public long getFirstLogin() {
        return firstLogin;
    }

    /**
     * This is for receiving teamChat messages or disabling team messages for each player (Used for staff members).
     *
     * @return If the player received team messages
     */
    public boolean isTeamChatLoggedIn() {
        return teamChatLogin;
    }

    /**
     * This is for disabling/enabling receiving report messages for each player (Used for staff members).
     *
     * @return If the player receives reports
     */
    public boolean isReportLoggedIn() {
        return reportLogin;
    }

    /**
     * Get stats from the player to show his activity
     *
     * @return Statistics from the player
     */
    public PlayerStats getStats() {
        return stats;
    }

    /**
     * The online time will be calculated live
     *
     * @return The online time from the player in milliseconds
     */
    public long getOnlineTime(){
        return onlineTime;
    }

    /**
     *
     * @return The history (bans/Mutes usw.) from the player
     */
    public History getHistory() {
        return history;
    }

    /**
     * Add custome properties to very player, don't forget to save with @saveProperties
     *
     * @return The player properties
     */
    public Document getProperties() {
        return properties;
    }

    /**
     * Get the online sessions from the player, we recommend not saving to much sessions.
     *
     * @return All available online sessions as list
     */
    public List<OnlineSession> getOnlineSessions() {
        return onlineSessions;
    }

    /**
     * Get all report which are created fo this player
     *
     * @return All report for this player.
     */
    public List<Report> getReports(){
        return this.reports;
    }

    /**
     * The ips are from the online sessions
     *
     * @return all available ips as list
     */
    public List<String> getIPs(){
        List<String> ips = new ArrayList<>();
        GeneralUtil.iterateAcceptedForEach(this.onlineSessions, object -> !ips.contains(object.getIp()), object ->ips.add(object.getIp()));
        return ips;
    }

    /**
     * An report is open, when no staff is processing this report (staff uuid is null).
     *
     * @return All open reports for this player as list (No staff is checking this)
     */
    public List<Report> getOpenReports(){
        return GeneralUtil.iterateAcceptedReturn(this.reports, object -> object.getStaff() == null);
    }

    /**
     * An report is in processing state the a stuff member is checking this player (staff uuid is not null).
     *
     * @return All reports which are processing by a staff member
     */
    public List<Report> getProcessingReports(){
        return GeneralUtil.iterateAcceptedReturn(this.reports, object -> object.getStaff() != null);
    }

    /**
     * Get one random processing report.
     *
     * @return A random processing report
     */
    public Report getProcessingReport(){
        List<Report> reports = getProcessingReports();
        if(reports.size() > 0) return reports.get(0);
        return null;
    }

    /**
     * Get one random open report
     *
     * @return A random open report.
     */
    public Report getOpenReport(){
        List<Report> reports = getOpenReports();
        if(reports.size() >= 1) return reports.get(0);
        return null;
    }

    /**
     * Get the staff, which is processing the reports for this player.
     *
     * @return The uuid of from the staff member
     */
    public UUID getReportStaff(){
        Report report = getProcessingReport();
        if(report != null) return report.getStaff();
        return null;
    }

    /**
     * Returns only one open report, when no other report is in processing state.
     *
     * @return An open report when no other report is in processing state
     */
    public Report getOpenReportWhenNoProcessing(){
        List<Report> reports = getReports();
        if(reports.size() >= 1 && reports.size() == this.reports.size()) return reports.get(0);
        return null;
    }

    /**
     * Get the report for this player from a player.
     *
     * @param reporter The reporter
     * @return The report, which is created by this reporter
     */
    public Report getReport(NetworkPlayer reporter) {
        return getReport(reporter.getUUID());
    }

    /**
     * Get the report for this player from a player.
     *
     * @param reporter The UUID of the reporter
     * @return The report, which is created by this reporter
     */
    public Report getReport(UUID reporter){
        return GeneralUtil.iterateOne(this.reports, object -> object.getReporterUUID().equals(reporter));
    }

    /**
     * Check if a player has created an report for this player.
     *
     * @param reporter the reporter
     * @return if rhe reporter created a report
     */
    public boolean hasReport(NetworkPlayer reporter){
        return getReport(reporter) != null;
    }

    /**
     * Check if a player has created an report for this player.
     *
     * @param reporter the uuid from the reporter
     * @return if rhe reproter created a report
     */
    public boolean hasReport(UUID reporter){
        return getReport(reporter) != null;
    }

    /**
     * Get the active ban of this player.
     *
     * @param type The banType (Chat or Network)
     * @return The current Ban
     */
    public Ban getBan(BanType type){
        return history.getBan(type);
    }


    /**
     * If this player has bypass, this is used for ban or report bypassing.
     *
     * @return if the player has bypass
     */
    public boolean hasBypass() {
        return bypass;
    }

    /**
     *
     * @return if this player is banned or not (Every type)
     */
    public boolean isBanned(){
        return history.isBanned();
    }

    /**
     *
     * @param type The ban type (Chat or Network)
     * @return if this player is banned
     */
    public boolean isBanned(BanType type){
        return getBan(type) != null;
    }

    @SuppressWarnings("Only for databse id creation")
    public void setHistory(History history) {
        this.history = history;
    }

    @SuppressWarnings("Only for databse id creation")

    public void setReports(List<Report> reports) {
        this.reports = reports;
    }

    public void setOnlineSessions(List<OnlineSession> sessions) {
        this.onlineSessions = sessions;
    }

}
