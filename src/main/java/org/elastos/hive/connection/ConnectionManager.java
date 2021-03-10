/*
 * Copyright (c) 2019 Elastos Foundation
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package org.elastos.hive.connection;

import org.elastos.hive.exception.HiveRException;
import org.elastos.hive.network.*;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Manage connection and network api.
 */
public class ConnectionManager {
	private Map<String, Object> apis = new HashMap<>();

	private String vaultBaseUrl;
	private BaseServiceConfig vaultConfig = new BaseServiceConfig.Builder().build() ;

	public ConnectionManager(String baseUrl, BaseServiceConfig baseServiceConfig) {
		resetVaultApi(baseUrl, baseServiceConfig);
	}

	public <T> T getNetworkApi(Class<T> apiCls) {
		String name = apiCls.getName();
		if (!name.endsWith("Api")) {
			throw new HiveRException("Must provide network api class.");
		}
		if (apis.get(name) == null) {
			apis.put(name, BaseServiceUtil.createService(apiCls,
					this.vaultBaseUrl, this.vaultConfig));
		}
		return (T)apis.get(name);
	}

	private void updateVaultConfig(BaseServiceConfig vaultConfig) {
		this.vaultConfig = vaultConfig;
	}

	private void updateVaultBaseUrl(String vaultBaseUrl) {
		this.vaultBaseUrl = vaultBaseUrl;
	}

	public void resetVaultApi(String baseUrl, BaseServiceConfig baseServiceConfig) {
		apis.clear();
		updateVaultBaseUrl(baseUrl);
		updateVaultConfig(baseServiceConfig);
	}

	public String getVaultBaseUrl() {
		return this.vaultBaseUrl;
	}

	public String getAccessToken() {
		return this.vaultConfig.getHeaderConfig().getAuthToken().getAccessToken();
	}

	public HttpURLConnection openURLConnection(String path) throws IOException {
		String url = this.getVaultBaseUrl() + UploadApi.API_PATH + path;
		HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(url).openConnection();
		httpURLConnection.setRequestMethod("POST");
		httpURLConnection.setRequestProperty("User-Agent",
				"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
		httpURLConnection.setConnectTimeout(5000);
		httpURLConnection.setReadTimeout(5000);

		httpURLConnection.setDoOutput(true);
		httpURLConnection.setDoInput(true);
		httpURLConnection.setUseCaches(false);
		httpURLConnection.setRequestProperty("Transfer-Encoding", "chunked");
		httpURLConnection.setRequestProperty("Connection", "Keep-Alive");
		httpURLConnection.setRequestProperty("Authorization", "token " + this.getAccessToken());

		httpURLConnection.setChunkedStreamingMode(0);

		return httpURLConnection;
	}

}
