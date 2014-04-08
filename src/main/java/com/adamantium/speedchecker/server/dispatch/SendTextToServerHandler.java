/**
 * Copyright 2011 ArcBees Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.adamantium.speedchecker.server.dispatch;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adamantium.speedchecker.shared.dispatch.SendTextToServerAction;
import com.adamantium.speedchecker.shared.dispatch.SendTextToServerResult;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.gwtplatform.dispatch.server.ExecutionContext;
import com.gwtplatform.dispatch.server.actionhandler.ActionHandler;
import com.gwtplatform.dispatch.shared.ActionException;

/**
 * Обработчик посылаемого текста
 *
 */
public class SendTextToServerHandler implements ActionHandler<SendTextToServerAction, SendTextToServerResult> {
    private ServletContext servletContext;
    /**
     * формат для отправляемоей даты
     */
    private SimpleDateFormat format = new SimpleDateFormat("yy-MM-dd HH:mm:ss");
    /**
     * Jetty использует slf4j логирование
     */
    final Logger logger = LoggerFactory.getLogger(SendTextToServerHandler.class);
    @Inject
    SendTextToServerHandler(ServletContext servletContext,
                            Provider<HttpServletRequest> requestProvider) {
        this.servletContext = servletContext;
    }

    /**
     * Обработчик запросов
     */
    @Override
    public SendTextToServerResult execute(SendTextToServerAction action, ExecutionContext context)
            throws ActionException {
        String input = action.getTextToServer();
        // по сути так должно быть реализовано логирование
        //servletContext.log("Text: "+input);

        // но у меня работает так
        logger.info("Text: "+input);

        // отправка результата
        return new SendTextToServerResult(format.format(new Date()));
    }

    @Override
    public Class<SendTextToServerAction> getActionType() {
        return SendTextToServerAction.class;
    }

    @Override
    public void undo(SendTextToServerAction action, SendTextToServerResult result, ExecutionContext context)
            throws ActionException {
        // Not undoable
    }
}
