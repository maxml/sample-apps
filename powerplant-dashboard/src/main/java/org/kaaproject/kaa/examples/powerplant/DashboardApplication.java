/**
 *  Copyright 2014-2016 CyberVision, Inc.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.kaaproject.kaa.examples.powerplant;

import java.io.IOException;
import java.net.URI;

import org.glassfish.grizzly.http.server.CLStaticHttpHandler;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.kaaproject.kaa.examples.powerplant.mappers.SimpleMapperProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class DashboardApplication {
    
    private static final Logger LOG = LoggerFactory.getLogger(DashboardApplication.class);

    public static void main(String[] args) throws IOException{
        LOG.info("Starting service");
        
        if(args.length != 2){
            System.err.println("USAGE: java -jar PowerplantDashboard.jar bind_host bind_port");
            return;
        }
        String baseURI = "http://" + args[0] + ":" + args[1] + "/api";
        LOG.info("Base URI: {}", baseURI);

        final ResourceConfig rc = new ResourceConfig().packages("org.kaaproject.kaa.examples.powerplant.resources");
        rc.register(JacksonFeature.class);
        rc.register(SimpleMapperProvider.class);

        HttpServer server = GrizzlyHttpServerFactory.createHttpServer(URI.create(baseURI), rc, false);
        server.getServerConfiguration().addHttpHandler(new CLStaticHttpHandler(DashboardApplication.class.getClassLoader(), "/"),"/");

        server.start();
        
        System.in.read();

        server.shutdownNow();
    }
}
