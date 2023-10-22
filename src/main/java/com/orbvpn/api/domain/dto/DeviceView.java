package com.orbvpn.api.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.orbvpn.api.domain.enums.DeviceAppType;
import com.orbvpn.api.domain.enums.DeviceOsType;
import com.orbvpn.api.service.DeviceService;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
public class DeviceView {
    String deviceInfo;
    DeviceAppType deviceAppType;
    DeviceOsType deviceOsType;
    DeviceIdView deviceIdView;
    String appVersion;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ssz")
    private LocalDateTime lastConnectionStartTime;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ssz")
    private LocalDateTime lastConnectionStopTime;
    Boolean isConnected;
    String lastSessionId;
    private Boolean isDeactivated;

    Integer lastConnectedServerId;
    String lastConnectedServerCountry;
    String lastConnectedServerCity;

    /**
     * this method is used by {@link com.orbvpn.api.mapper.DeviceMapper}
     */
    public void setLastConnectionStopTime(LocalDateTime lastConnectionStopTime) {
        this.lastConnectionStopTime = lastConnectionStopTime;
        this.isConnected = lastConnectionStopTime == null;
    }

    /**
     * this method is used by {@link com.orbvpn.api.mapper.DeviceMapper}
     */
    public void setDeviceInfo(String deviceInfo) {
        this.deviceInfo = deviceInfo;
        Integer firstIdx = deviceInfo.indexOf(DeviceService.SEPARATOR);
        Integer lastIdx = deviceInfo.lastIndexOf(DeviceService.SEPARATOR);

        /**
         * Determining Device App Type
         */
        String os = null;
        String id = null;
        String ver = null;
        if (firstIdx == -1 || lastIdx == -1) {
            deviceAppType = DeviceAppType.EXTERNAL;
        } else {
            os = deviceInfo.substring(0, firstIdx);
            id = deviceInfo.substring(firstIdx + DeviceService.SEPARATOR.length(), lastIdx);
            ver = deviceInfo.substring(lastIdx + DeviceService.SEPARATOR.length());

            if (os == null || id == null || ver == null) {
                deviceAppType = DeviceAppType.EXTERNAL;
            } else {
                deviceAppType = DeviceAppType.INTERNAL;
            }
        }

        /**
         * Internal App
         */
        if (deviceAppType == DeviceAppType.INTERNAL) {
            appVersion = ver.trim();
            os = os.trim().toUpperCase();
            deviceOsType = DeviceOsType.valueOf(os);

            deviceIdView = new DeviceIdView();
            switch (deviceOsType) {
                case IOS:
                    deviceIdView.setUuid(id);
                    break;
                case ANDROID:
                    deviceIdView.setSerial(id);
                    break;
                case MACOS:
                case WINDOWS:
                    deviceIdView.setName(id);
                    break;
                default:
                    deviceIdView.setName(id);
            }
        } else if (deviceAppType == DeviceAppType.EXTERNAL) {
            for (DeviceOsType osType : DeviceOsType.values()) {
                if (deviceInfo.toUpperCase().contains(osType.getExternalAppAlternateName().toUpperCase())) {
                    deviceOsType = osType;
                }
            }
        }
    }
}
