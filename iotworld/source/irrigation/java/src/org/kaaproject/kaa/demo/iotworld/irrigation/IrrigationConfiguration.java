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

package org.kaaproject.kaa.demo.iotworld.irrigation;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.kaaproject.kaa.client.configuration.base.ConfigurationListener;
import org.kaaproject.kaa.schema.base.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IrrigationConfiguration implements ConfigurationListener {

    private static final Logger LOG = LoggerFactory.getLogger(IrrigationConfiguration.class);
    private static final IrrigationConfiguration instance = new IrrigationConfiguration();

    private static final String DEFAULT_PROFILE_FILE_NAME = "profile";
    private static final String DEFAULT_PROFILE_PATH = System.getProperty("java.io.tmpdir");

    private static final String DEFAULT_NAME = "Irrigation management driver";
    private static final String DEFAULT_MODEL = "Raspberry PI 2B";

    private static final long DEFAULT_IRRIGATION_DURATION_TIME_MS = TimeUnit.SECONDS.toMillis(3);
    private static final long MAX_IRRIGATION_PERIOD = TimeUnit.MINUTES.toMillis(5);
    private static final long DEFAULT_IRRIGATION_PERIOD = MAX_IRRIGATION_PERIOD;

    private static final float DEFAULT_WATER_FLOW_SENSOR_COEFFICIENT = 1.4f;
    private static final int DEFAULT_WEB_PORT = 7080;

    private static final int DEFAULT_MONTHLY_SPENT_WATER = 540;
    private static final int DEFAULT_REMAINING_WATER = 90000;

    private final Properties persistedProperties;
    private static final String IS_ATTACHED_TO_USER = "is_attached_to_user";
    private static final String DEVICE_NAME = "device_name";
    private final Path PROPERTY_PATH = Paths.get("irrigation.state");

    private String profilePath;
    private Integer webPort;
    private Float waterFlowSensorCoefficient;
    private Long defaultIrrigationDurationTime;
    private Long defaultIrrigationInterval;
    private Integer defaultMonthlySpentWater;
    private Integer defaultRemainingWater;

    public static IrrigationConfiguration getInstance() {
        return instance;
    }

    private IrrigationConfiguration() {
        LOG.debug("Init irrigation configuration.");
        persistedProperties = new Properties();
        if (Files.exists(PROPERTY_PATH)) {
            try {
                persistedProperties.load(new FileInputStream(PROPERTY_PATH.toFile()));
            } catch (IOException e) {
                LOG.error("Can't init state holder. Can't load property file.", e);
                throw new RuntimeException(e);
            }
        }
    }

    public String getDeviceName() {
        return persistedProperties.getProperty(DEVICE_NAME, DEFAULT_NAME);
    }

    public String getDeviceModelName() {
        return DEFAULT_MODEL;
    }

    public Integer getWebPort() {
        if (webPort == null) {
            webPort = DEFAULT_WEB_PORT;
        }
        return webPort;
    }

    public void setWebPort(Integer webPort) {
        this.webPort = webPort;
    }

    public Float getWaterFlowSensorCoefficient() {
        if (waterFlowSensorCoefficient == null) {
            waterFlowSensorCoefficient = DEFAULT_WATER_FLOW_SENSOR_COEFFICIENT;
        }
        return waterFlowSensorCoefficient;
    }

    public void setWaterFlowSensorCoefficient(Float waterFlowSensorCoefficient) {
        this.waterFlowSensorCoefficient = waterFlowSensorCoefficient;
    }

    public long getIrrigationDurationTime() {
        if (defaultIrrigationDurationTime == null) {
            defaultIrrigationDurationTime = DEFAULT_IRRIGATION_DURATION_TIME_MS;
        }
        return defaultIrrigationDurationTime;
    }

    public void setDefaultIrrigationDurationTime(long defaultIrrigationDurationTime) {
        this.defaultIrrigationDurationTime = defaultIrrigationDurationTime;
    }

    public long getIrrigationIntervalMs() {
        if (defaultIrrigationInterval == null) {
            defaultIrrigationInterval = DEFAULT_IRRIGATION_PERIOD;
        }
        return defaultIrrigationInterval;
    }

    public void setDefaultIrrigationInterval(long defaultIrrigationPeriod) {
        this.defaultIrrigationInterval = defaultIrrigationPeriod;
    }

    public void setProfilePath(String profilePath) {
        this.profilePath = profilePath;
    }

    @Override
    public void onConfigurationUpdate(Configuration configuration) {
        LOG.debug("Got configuration update {}", configuration);
    }

    public Path getProfilePath() {
        String path;
        if (profilePath != null && profilePath.length() > 0) {
            path = profilePath;
        } else {
            path = DEFAULT_PROFILE_PATH;
        }
        return Paths.get(path, DEFAULT_PROFILE_FILE_NAME);
    }

    public Integer getDefaultMonthlySpentWater() {
        if (defaultMonthlySpentWater == null) {
            defaultMonthlySpentWater = DEFAULT_MONTHLY_SPENT_WATER;
        }
        return defaultMonthlySpentWater;
    }

    public Integer getDefaultRemainingWater() {
        if (defaultRemainingWater == null) {
            defaultRemainingWater = DEFAULT_REMAINING_WATER;
        }
        return defaultRemainingWater;
    }

    private OutputStream getPropertyOutputStream() {
        OutputStream out = null;
        try {
            out = Files.newOutputStream(PROPERTY_PATH, new OpenOption[] { StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING });
        } catch (IOException e) {
            LOG.error("Can't get output stream from property file", e);
        }
        return out;
    }

    public void onAttachtoUser() {
        persistedProperties.put(IS_ATTACHED_TO_USER, true);
    }

    public void onDetachFromUser() {
        persistedProperties.put(IS_ATTACHED_TO_USER, false);
    }

    public void storeDeviceName(String deviceName) {
        persistedProperties.put(DEVICE_NAME, deviceName);
        storeProperties();
    }

    private void storeProperties() {
        try {
            persistedProperties.store(getPropertyOutputStream(), "Store property for irrigation system");
        } catch (IOException e) {
            LOG.error("Got error during property persistence.", e);
            throw new RuntimeException(e);
        }
    }
}
