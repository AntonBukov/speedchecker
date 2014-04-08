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

import java.util.Date;
import java.util.Random;

import com.adamantium.speedchecker.client.place.NameTokens;
import com.adamantium.speedchecker.shared.FieldVerifier;
import com.adamantium.speedchecker.shared.dispatch.SendTextToServerAction;
import com.adamantium.speedchecker.shared.dispatch.SendTextToServerResult;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.dispatch.shared.DispatchAsync;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyStandard;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;

/**
 * Реализация обработчика операций
 * 
 */
public class ApplicationPresenter extends
		Presenter<ApplicationPresenter.AppView, ApplicationPresenter.MyProxy>
		implements ApplicationUiHandlers {
	/**
	 * {@link ApplicationPresenter}'s proxy.
	 */
	@ProxyStandard
	@NameToken(NameTokens.home)
	public interface MyProxy extends ProxyPlace<ApplicationPresenter> {
	}

	/**
	 * Таймер для отправки пакетов
	 */
	private Timer timer = null;

	/**
	 * {@link ApplicationPresenter}'s view.
	 */
	public interface AppView extends View, HasUiHandlers<ApplicationUiHandlers> {
		void resetAndFocus();

		void disableInterface();

		void setError(String errorText);

		void addServerResponse(long mSec, String response);
	}

	/**
	 * обработчик запросов
	 */
	private final DispatchAsync dispatcher;

	@Inject
	ApplicationPresenter(EventBus eventBus, AppView view, MyProxy proxy,
			DispatchAsync dispatcher) {
		super(eventBus, view, proxy, RevealType.Root);

		this.dispatcher = dispatcher;

		getView().setUiHandlers(this);
	}

	/**
	 * валидация значения и настройка таймера
	 */
	@Override
	public void start(final int size) {
		// First, we validate the input.
		getView().setError("");
		if (!FieldVerifier.isValidSize(size)) {
			getView().setError(
					"Packet Size should be greater then 16 and less then 1024");
			return;
		}

		if (timer == null) {
			timer = new Timer() {
				@Override
				public void run() {
					// символы для генератора
					String chars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
					Random rnd = new Random();
					StringBuilder sb = new StringBuilder(size);
					for (int i = 0; i < size; i++) {
						//получение случайного символа
						sb.append(chars.charAt(rnd.nextInt(chars.length())));
					}
					String text = sb.toString();
					sendTextToServer(text);
				}
			};

			//секундный интервал
			timer.scheduleRepeating(1000);
			getView().disableInterface();
		}
	}

	/**
	 * отправка пакета
	 */
	private void sendTextToServer(final String text) {
		//время начала отправки
		final long time = new Date().getTime();
		dispatcher.execute(new SendTextToServerAction(text),
				new AsyncCallback<SendTextToServerResult>() {
					@Override
					public void onFailure(Throwable caught) {
						getView().addServerResponse(
								new Date().getTime() - time,
								"An error occured: " + caught.getMessage());
					}

					@Override
					public void onSuccess(SendTextToServerResult result) {
						//расчет времени на обработку
						getView().addServerResponse(
								new Date().getTime() - time,
								result.getResponse());
					}
				});
	}

	/**
	 * остановка таймера
	 */
	@Override
	public void stop() {
		if (timer != null) {
			timer.cancel();
			timer = null;
		}
	}
}
