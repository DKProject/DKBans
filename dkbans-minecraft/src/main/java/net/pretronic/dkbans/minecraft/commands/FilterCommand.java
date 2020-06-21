/*
 * (C) Copyright 2020 The DKBans Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 14.06.20, 15:15
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

package net.pretronic.dkbans.minecraft.commands;

import net.pretronic.dkbans.api.DKBans;
import net.pretronic.dkbans.api.filter.Filter;
import net.pretronic.dkbans.api.filter.FilterManager;
import net.pretronic.dkbans.minecraft.config.Messages;
import net.pretronic.libraries.command.command.BasicCommand;
import net.pretronic.libraries.command.command.configuration.CommandConfiguration;
import net.pretronic.libraries.command.sender.CommandSender;
import net.pretronic.libraries.message.bml.variable.VariableSet;
import net.pretronic.libraries.utility.GeneralUtil;
import net.pretronic.libraries.utility.StringUtil;
import net.pretronic.libraries.utility.interfaces.ObjectOwner;

import java.util.Collection;

public class FilterCommand extends BasicCommand {

    public FilterCommand(ObjectOwner owner, CommandConfiguration configuration) {
        super(owner, configuration);
    }

    @Override
    public void execute(CommandSender sender, String[] arguments) {
        FilterManager manager = DKBans.getInstance().getFilterManager();
        if(arguments.length >= 1){
            String argument = arguments[0];
            if(StringUtil.equalsOne(argument,"reload","rl")){

            }else if(StringUtil.equalsOne(argument,"list","l")){

                Collection<Filter> filters;
                if(arguments.length >= 2){
                    String area = arguments[1];
                    if(!manager.hasAffiliationArea(area)){
                        sender.sendMessage(Messages.COMMAND_FILTER_AFFILIATION_AREA_NOT_FOUND, VariableSet.create()
                                .add("area",area));
                        return;
                    }
                    filters = manager.getFilters(area);
                }else{
                    filters = manager.getFilters();
                }
                sender.sendMessage(Messages.COMMAND_FILTER_LIST,VariableSet.create()
                        .addDescribed("filters",filters));
                return;

            }else if(StringUtil.equalsOne(argument,"add","create","a","c") && arguments.length >= 3){

                String area = arguments[1];
                String value = arguments[2];
                String operation = arguments.length >= 4 ? arguments[3] : "EQUALS";

                if(!manager.hasAffiliationArea(area)){
                    sender.sendMessage(Messages.COMMAND_FILTER_AFFILIATION_AREA_NOT_FOUND, VariableSet.create()
                            .add("area",area));
                    return;
                }

                if(!manager.hasOperationFactory(operation)){
                    sender.sendMessage(Messages.COMMAND_FILTER_OPERATION_NOT_FOUND, VariableSet.create()
                            .add("operation",operation));
                    return;
                }

                Filter filter = manager.createFilter(area,value,operation);

                sender.sendMessage(Messages.COMMAND_FILTER_DELETED,VariableSet.create()
                        .addDescribed("filter",filter));

            }else if(StringUtil.equalsOne(argument,"remove","delete","r","d") && arguments.length >= 2){

                String rawId = arguments[1];
                if(!GeneralUtil.isNaturalNumber(rawId)){
                    sender.sendMessage(Messages.ERROR_INVALID_NUMBER,VariableSet.create()
                            .add("number",rawId));
                    return;
                }
                int id = Integer.parseInt(rawId);

                Filter filter = manager.getFilter(id);
                if(filter == null){
                    sender.sendMessage(Messages.COMMAND_FILTER_NOT_FOUND,VariableSet.create()
                            .add("id",id));
                    return;
                }

                manager.deleteFilter(filter);
                sender.sendMessage(Messages.COMMAND_FILTER_DELETED,VariableSet.create()
                        .addDescribed("filter",filter));
            }
        }
        sender.sendMessage(Messages.COMMAND_FILTER_HELP);
    }
}
