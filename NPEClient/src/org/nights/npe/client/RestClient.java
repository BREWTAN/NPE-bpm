package org.nights.npe.client;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.nights.npe.api.ContextData;
import org.nights.npe.api.ResponseStatus;
import org.nights.npe.api.SubmitStates;

public class RestClient {

	String baseurl;
	CloseableHttpClient httpClient;

	public RestClient(String host, int port, String baseurl) {
		PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
		// Increase max total connection to 200
		cm.setMaxTotal(200);
		// Increase default max connection per route to 20
		// cm.setDefaultMaxPerRoute(20);
		// Increase max connections for localhost:80 to 50
		this.baseurl = "http://" + host + ":" + port + baseurl;
		// cm.setMaxPerRoute(new HttpRoute(localhost), 50);
		httpClient = HttpClients.custom().setConnectionManager(cm).build();
	}

	public String getContentAsString(HttpEntity entity)
			throws IllegalStateException, IOException {
		if (entity != null) {
			InputStream instream = null;
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			try {
				instream = entity.getContent();
				byte bb[] = new byte[512];
				int rsize = 0;
				while ((rsize = instream.read(bb)) > 0) {
					bout.write(bb, 0, rsize);
				}
				return new String(bout.toByteArray());
			} catch (Exception e) {

			} finally {
				if (instream != null)
					instream.close();
				bout.close();
			}
		}
		return "";

	}

	public SubmitStates obtainTask(String role, String obtainer, String center) {
		String query = "?";
		if (role != null && role.length() > 0)
			query += "role=" + role + "&";
		query += "obtainer=" + obtainer + "&center=" + center;
		String body = doGet("nperest/obtain" + query);
		return JsonUtil.json2Bean(body, SubmitStates.class);
	}

	public ResponseStatus submitTask(SubmitStates state) {
		String post = JsonUtil.bean2Json(state).toString();
		String body = doPost("nperest/submit", post);
		return JsonUtil.json2Bean(body, ResponseStatus.class);
	}

	public ResponseStatus newProcess(String procdef, String submiter,
			String center, ContextData ctxData) {
		String postdata = JsonUtil.bean2Json(ctxData).toString();
//		System.out.println("postData==:"+postdata);
		String body = doPost("nperest/newproc?procdef=" + procdef
				+ "&submiter=" + submiter + "&center=" + center, postdata);
//		System.out.println("postDatabody==:"+body);
		return JsonUtil.json2Bean(body, ResponseStatus.class);
	}

	public String doGet(String path) throws RuntimeException {
		HttpGet httpget = new HttpGet(baseurl + path);
		CloseableHttpResponse response = null;
		try {
			response = httpClient.execute(httpget);
			return getContentAsString(response.getEntity());
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		} finally {
			if (response != null)
				try {
					response.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
	}

	public String doPost(String path, String body) throws RuntimeException {
		HttpPost post = new HttpPost(baseurl + path);
		CloseableHttpResponse response = null;
		try {
			post.setEntity(new StringEntity(body));
			post.setHeader("Content-type", "application/json; charset=utf-8");
			response = httpClient.execute(post);
			return getContentAsString(response.getEntity());
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		} finally {
			if (response != null)
				try {
					response.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
	}
	
}
