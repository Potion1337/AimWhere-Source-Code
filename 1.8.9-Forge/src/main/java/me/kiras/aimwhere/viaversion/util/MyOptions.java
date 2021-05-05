package me.kiras.aimwhere.viaversion.util;

import me.kiras.aimwhere.viaversion.ViaVersion;
import net.minecraft.util.MathHelper;

public class MyOptions {
    public static int curViaVersion = 47;
    public static int curViaVersionStage = 9;
    public static int getViaVersionByInt(int id) {
        switch (id) {
            case 0: {
                return 51;
            }
            case 1: {
                return 60;
            }
            case 2: {
                return 61;
            }
            case 3: {
                return 73;
            }
            case 4: {
                return 74;
            }
            case 5: {
                return 77;
            }
            case 6: {
                return 78;
            }
            case 7: {
                return 4;
            }
            case 8: {
                return 5;
            }
            case 9: {
                return 47;
            }
            case 10: {
                return 107;
            }
            case 11: {
                return 108;
            }
            case 12: {
                return 109;
            }
            case 13: {
                return 110;
            }
            case 14: {
                return 210;
            }
            case 15: {
                return 315;
            }
            case 16: {
                return 316;
            }
            case 17: {
                return 335;
            }
            case 18: {
                return 338;
            }
            case 19: {
                return 340;
            }
            case 20: {
                return 393;
            }
            case 21: {
                return 401;
            }
            case 22: {
                return 404;
            }
            case 23: {
                return 477;
            }
            case 24: {
                return 480;
            }
            case 25: {
                return 485;
            }
            case 26: {
                return 490;
            }
            case 27: {
                return 498;
            }
            case 28: {
                return 573;
            }
            case 29: {
                return 575;
            }
            case 30: {
                return 578;
            }
            case 31: {
                return 735;
            }
            case 32: {
                return 736;
            }
            case 33: {
                return 751;
            }
            case 34: {
                return 753;
            }
            case 35: {
                return 754;
            }
            case 36: {
                return 755;
            }
        }
        return 47;
    }
    public static void setOptionFloatValue(Options settingsOption, float value)
    {
        if (settingsOption == Options.VIAVERSION) {
            try {
                curViaVersion = getViaVersionByInt(((int)value));
                curViaVersionStage = (int)value;
                ViaVersion.clientSideVersion = getViaVersionByInt(((int)value));
            }
            catch (Exception e) {
                curViaVersion = 47;
                curViaVersionStage = 9;
            }
        }
    }
    public static String getKeyBinding() {
        return "Version: " + ProtocolUtils.getProtocolName(curViaVersion);
    }
    public float getOptionFloatValue()
    {
        return curViaVersionStage;
    }
    public enum Options
    {
        VIAVERSION("ViaVersion", true, false, 0.0f, 36.0f, 1.0f);
        private final boolean enumFloat;
        private final boolean enumBoolean;
        private final String enumString;
        private float valueMax;
        private final float valueStep;
        private float valueMin;
        public float normalizeValue(float p_148266_1_)
        {
            return MathHelper.clamp_float((this.snapToStepClamp(p_148266_1_) - this.valueMin) / (this.valueMax - this.valueMin), 0.0F, 1.0F);
        }
        public float denormalizeValue(float p_148262_1_)
        {
            return this.snapToStepClamp(this.valueMin + (this.valueMax - this.valueMin) * MathHelper.clamp_float(p_148262_1_, 0.0F, 1.0F));
        }
        public float snapToStepClamp(float p_148268_1_)
        {
            p_148268_1_ = this.snapToStep(p_148268_1_);
            return MathHelper.clamp_float(p_148268_1_, this.valueMin, this.valueMax);
        }

        protected float snapToStep(float p_148264_1_)
        {
            if (this.valueStep > 0.0F)
            {
                p_148264_1_ = this.valueStep * (float)Math.round(p_148264_1_ / this.valueStep);
            }

            return p_148264_1_;
        }

        public static Options getEnumOptions(int p_74379_0_)
        {
            for (Options gamesettings$options : values())
            {
                if (gamesettings$options.returnEnumOrdinal() == p_74379_0_)
                {
                    return gamesettings$options;
                }
            }

            return null;
        }

        Options(String p_i45004_3_, boolean p_i45004_4_, boolean p_i45004_5_, float p_i45004_6_, float p_i45004_7_, float p_i45004_8_)
        {
            this.enumString = p_i45004_3_;
            this.enumFloat = p_i45004_4_;
            this.enumBoolean = p_i45004_5_;
            this.valueMin = p_i45004_6_;
            this.valueMax = p_i45004_7_;
            this.valueStep = p_i45004_8_;
        }

        public boolean getEnumFloat()
        {
            return this.enumFloat;
        }

        public boolean getEnumBoolean()
        {
            return this.enumBoolean;
        }

        public int returnEnumOrdinal()
        {
            return this.ordinal();
        }

        public String getEnumString()
        {
            return this.enumString;
        }

        public float getValueMax()
        {
            return this.valueMax;
        }

        public void setValueMax(float p_148263_1_)
        {
            this.valueMax = p_148263_1_;
        }
    }
}
