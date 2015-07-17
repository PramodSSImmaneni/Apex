/**
 * Copyright (C) 2015 DataTorrent, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.datatorrent.stram.security;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.http.FilterContainer;
import org.apache.hadoop.http.FilterInitializer;
import org.apache.hadoop.http.HttpConfig;
import org.apache.hadoop.net.NetUtils;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.webapp.util.WebAppUtils;

import com.datatorrent.stram.util.ConfigUtils;

/**
 * Based on org.apache.hadoop.yarn.server.webproxy.amfilter.AmFilterIntializer
 * See https://issues.apache.org/jira/browse/YARN-1517
 *
 * @since 0.9.2
 */
public class StramWSFilterInitializer extends FilterInitializer
{
  private static final String FILTER_NAME = "AM_PROXY_FILTER";
  private static final String FILTER_CLASS = StramWSFilter.class.getCanonicalName();

  @Override
  public void initFilter(FilterContainer container, Configuration conf) {
    Map<String, String> params = new HashMap<String, String>();
    Collection<String> proxies = new ArrayList<String>();
    if (ConfigUtils.isRMHAEnabled(conf)) {
      // HA is enabled get all
      for (String rmId : ConfigUtils.getRMHAIds(conf)) {
        proxies.add(getResolvedRMWebAppURLWithoutScheme(conf, rmId));
      }
    } else {
      proxies.add(WebAppUtils.getProxyHostAndPort(conf));
    }
    StringBuilder proxyBr = new StringBuilder();
    for (String proxy : proxies) {
      if (proxyBr.length() != 0) {
        proxyBr.append(StramWSFilter.PROXY_DELIMITER);
      }
      String[] parts = proxy.split(":");
      proxyBr.append(parts[0]);
    }
    params.put(StramWSFilter.PROXY_HOST, proxyBr.toString());
    container.addFilter(FILTER_NAME, FILTER_CLASS, params);
  }

  // From org.apache.hadoop.yarn.webapp.util.WebAppUtils
  // Modified for HA support
  // Replace with methods from Hadoop when HA support is available
  public static String getResolvedRMWebAppURLWithoutScheme(Configuration conf, String rmId) {
    return getResolvedRMWebAppURLWithoutScheme(conf,
            HttpConfig.isSecure() ? HttpConfig.Policy.HTTPS_ONLY : HttpConfig.Policy.HTTP_ONLY, rmId);
  }

  // From org.apache.hadoop.yarn.webapp.util.WebAppUtils
  // Modified for HA support
  public static String getResolvedRMWebAppURLWithoutScheme(Configuration conf,
                                                           HttpConfig.Policy httpPolicy, String rmId) {
    InetSocketAddress address = null;
    if (httpPolicy == HttpConfig.Policy.HTTPS_ONLY) {
      address =
              conf.getSocketAddr(YarnConfiguration.RM_WEBAPP_HTTPS_ADDRESS + "." + rmId,
                      YarnConfiguration.DEFAULT_RM_WEBAPP_HTTPS_ADDRESS,
                      YarnConfiguration.DEFAULT_RM_WEBAPP_HTTPS_PORT);
    } else {
      address =
              conf.getSocketAddr(YarnConfiguration.RM_WEBAPP_ADDRESS + "." + rmId,
                      YarnConfiguration.DEFAULT_RM_WEBAPP_ADDRESS,
                      YarnConfiguration.DEFAULT_RM_WEBAPP_PORT);
    }
    address = NetUtils.getConnectAddress(address);
    StringBuffer sb = new StringBuffer();
    InetAddress resolved = address.getAddress();
    if (resolved == null || resolved.isAnyLocalAddress() ||
            resolved.isLoopbackAddress()) {
      String lh = address.getHostName();
      try {
        lh = InetAddress.getLocalHost().getCanonicalHostName();
      } catch (UnknownHostException e) {
        //Ignore and fallback.
      }
      sb.append(lh);
    } else {
      sb.append(address.getHostName());
    }
    sb.append(":").append(address.getPort());
    return sb.toString();
  }

}
