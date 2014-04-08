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

package com.adamantium.speedchecker.client.application;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;

/**
 * Реализация представления страницы
 *
 */
public class ApplicationView extends ViewWithUiHandlers<ApplicationUiHandlers>
		implements ApplicationPresenter.AppView {
	interface Binder extends UiBinder<Widget, ApplicationView> {
	}

	/**
	 * поле размера пакета
	 */
	@UiField
	TextBox sizeField;
	/**
	 * Кнопка для запуска
	 */
	@UiField
	Button startButton;
	/**
	 * Место для вывода ошибок
	 */
	@UiField
	HTML error;
	
	/**
	 * Кнопка для остановки
	 */
	@UiField
	Button stopButton;
	/**
	 * Поле отображения информации
	 */
	@UiField
	TextArea responseTextArea;

	/**
	 * инициализация биндинга
	 * @param uiBinder
	 */
	@Inject
	ApplicationView(Binder uiBinder) {
		initWidget(uiBinder.createAndBindUi(this));
		resetAndFocus();
	}

	/**
	 * сброс формы
	 */
	@Override
	public void resetAndFocus() {
		sizeField.setFocus(true);
		sizeField.selectAll();
	}

	/**
	 * вывод ошибки
	 */
	@Override
	public void setError(String errorText) {
		error.setText(errorText);
	}

	/**
	 * обработка начала отправок
	 * @param event
	 */
	@UiHandler("startButton")
	void onStartButtonClick(ClickEvent event) {
		try{
			getUiHandlers().start(Integer.parseInt(sizeField.getText()));
		}catch(NumberFormatException e){
			setError("Packet Size should be a number");
		}
	}

	/**
	 * обработчик остановки
	 * @param event
	 */
	@UiHandler("stopButton")
	void onStopButtonClick(ClickEvent event) {
		getUiHandlers().stop();
		startButton.setVisible(true);
		stopButton.setVisible(false);
		sizeField.setEnabled(true);
	}

	/**
	 * добавление нового отвера сервера
	 */
	@Override
	public void addServerResponse(long mSec, String response) {
		StringBuilder buffer = new StringBuilder(responseTextArea.getText());
		responseTextArea.setText(buffer.append("".equals(responseTextArea.getText()) ? "" : "\n")
				.append(response).append(" time=").append(mSec).append(" ms").toString());
	}

	/**
	 * блокировка интерфейса
	 */
	@Override
	public void disableInterface() {
		sizeField.setEnabled(false);
		startButton.setVisible(false);
		stopButton.setVisible(true);
	}
}
