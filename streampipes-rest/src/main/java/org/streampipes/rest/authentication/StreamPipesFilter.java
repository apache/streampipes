/*
 * Copyright 2018 FZI Forschungszentrum Informatik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.streampipes.rest.authentication;

import org.apache.shiro.web.filter.authc.UserFilter;
import org.apache.shiro.web.util.WebUtils;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * Requires the requesting user to be authenticated for the request to continue and if they are not,
 * responds with HTML Error code 403.
 *
 * See https://stackoverflow.com/questions/30344441/shiro-filter-without-redirect/30344953#30344953
 *
 */

public class StreamPipesFilter extends UserFilter {
    private static final String message = "Access denied!";

    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        HttpServletResponse httpResponse ;
        try { httpResponse = WebUtils.toHttp(response); }

        catch (ClassCastException ex) {
            // Not a HTTP Servlet operation
            return super.onAccessDenied(request, response) ;
        }
        if ( message == null )
            httpResponse.sendError(403) ;
        else
            httpResponse.sendError(403, message) ;
        return false ;
    }
}
