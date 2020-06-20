package net.pretronic.dkbans.common.template.unpunishment;

import net.pretronic.dkbans.api.template.unpunishment.UnPunishmentTemplateEntryOption;

import java.util.Map;

public class DefaultUnPunishmentTemplateEntryOption implements UnPunishmentTemplateEntryOption {

    private final long removeTime;
    private final double divider;
    private final Map<String, Object> properties;

    public DefaultUnPunishmentTemplateEntryOption(long removeTime, double divider, Map<String, Object> properties) {
        this.removeTime = removeTime;
        this.divider = divider;
        this.properties = properties;
    }

    @Override
    public long getRemoveTime() {
        return this.removeTime;
    }

    @Override
    public double getDivider() {
        return this.divider;
    }

    @Override
    public Map<String, Object> getProperties() {
        return this.properties;
    }
}
