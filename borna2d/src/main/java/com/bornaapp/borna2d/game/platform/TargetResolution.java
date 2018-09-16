package com.bornaapp.borna2d.game.platform;

/**
 * Created by s. Mehdi HashemiNia on 1/17/2017.<br>
 * <p>
 * on android devices, screen resolution is forced by device hardware. some examples are:<br>
 * Samsung Galaxy J7, J5, A5, A3, S3. Huawei Honor 4c:   720x1280<br>
 * Samsung Galaxy S5. Huawei P8  :   1080x1920<br>
 * Samsung Galaxy S6, S7, Note4, Note5:   1440x2560<br>
 */
public class TargetResolution {
    private int width;
    private int height;
    public boolean isPortrait;

    public TargetResolution() {
        this.width = 1280;
        this.height = 720;
        this.isPortrait = true;
    }

    public TargetResolution(int width, int height, boolean isPortrait) {
        this.width = width;
        this.height = height;
        this.isPortrait = isPortrait;
    }

    public TargetResolution(Device device, boolean isPortrait) {
        switch (device) {

            case SamsungGalaxyJ7_J5_A5_A3_S3: //aspect ratio: 1.77
                width = 1280;
                height = 720;
                break;

            case SamsungGalaxyS4_S5_HuaweiP8: //aspect ratio: 1.77
                width = 1920;
                height = 1080;
                break;

            case SamsungGalaxyS6_S7_Note4_Note5_Note6_Note7: //aspect ratio: 1.77
                width = 2560;
                height = 1440;
                break;

            case SAMSUNG_GALAXY_S8_S8PLUS_S9_S9PLUS_NOTE8: //aspect ratio: 2.05
                width = 2960;
                height = 1440;
                break;

            case PC_DESKTOP:
                width = 640;
                height = 360;
                break;
        }
        this.isPortrait = isPortrait;
    }

    public int getWidth() {
        return (isPortrait ? height : width);
    }

    public int getHeight() {
        return (isPortrait ? width : height);
    }
}
