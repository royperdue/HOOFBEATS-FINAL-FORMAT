package com.hoofbeats.app.utility;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by royperdue on 6/9/17.
 */
public class ForceTracker
{
    private List<Float> forceReadingsLH = new ArrayList<>();
    private List<Float> forceReadingsLF = new ArrayList<>();
    private List<Float> forceReadingsRH = new ArrayList<>();
    private List<Float> forceReadingsRF = new ArrayList<>();

    private List<Float> averageForceReadingsLH = new ArrayList<>();
    private List<Float> averageForceReadingsLF = new ArrayList<>();
    private List<Float> averageForceReadingsRH = new ArrayList<>();
    private List<Float> averageForceReadingsRF = new ArrayList<>();

    private float currentForceReadingLH;
    private float currentForceReadingLF;
    private float currentForceReadingRH;
    private float currentForceReadingRF;

    public boolean isTurningPointUpLH()
    {
        int increase = 0;

        for (int i = 0; i < (averageForceReadingsLH.size() - 1); i++)
        {
            if (averageForceReadingsLH.get(i + 1) > averageForceReadingsLH.get(i))
            {
                increase++;
            } else if (averageForceReadingsLH.get(i + 1) < averageForceReadingsLH.get(i))
            {
                increase--;
            }
        }

        if (increase >= 3)
        {
            forceReadingsLH.clear();
            averageForceReadingsLH.clear();

            return true;
        }
        else
            return false;
    }

    public boolean isTurningPointDownLH()
    {
        int increase = 0;

        for (int i = 0; i < (averageForceReadingsLH.size() - 1); i++)
        {
            if (averageForceReadingsLH.get(i + 1) < averageForceReadingsLH.get(i))
            {
                increase++;
            } else if (averageForceReadingsLH.get(i + 1) > averageForceReadingsLH.get(i))
            {
                increase--;
            }
        }

        if (increase >= 3)
        {
            forceReadingsLH.clear();
            averageForceReadingsLH.clear();

            return true;
        }
        else
            return false;
    }

    public boolean isTurningPointUpLF()
    {
        int increase = 0;

        for (int i = 0; i < (averageForceReadingsLF.size() - 1); i++)
        {
            if (averageForceReadingsLF.get(i + 1) > averageForceReadingsLF.get(i))
            {
                increase++;
            } else if (averageForceReadingsLF.get(i + 1) < averageForceReadingsLF.get(i))
            {
                increase--;
            }
        }

        if (increase >= 3)
        {
            forceReadingsLF.clear();
            averageForceReadingsLF.clear();

            return true;
        }
        else
            return false;
    }

    public boolean isTurningPointDownLF()
    {
        int increase = 0;

        for (int i = 0; i < (averageForceReadingsLF.size() - 1); i++)
        {
            if (averageForceReadingsLF.get(i + 1) < averageForceReadingsLF.get(i))
            {
                increase++;
            } else if (averageForceReadingsLF.get(i + 1) > averageForceReadingsLF.get(i))
            {
                increase--;
            }
        }

        if (increase >= 3)
        {
            forceReadingsLF.clear();
            averageForceReadingsLF.clear();

            return true;
        }
        else
            return false;
    }

    public boolean isTurningPointUpRH()
    {
        int increase = 0;

        for (int i = 0; i < (averageForceReadingsRH.size() - 1); i++)
        {
            if (averageForceReadingsRH.get(i + 1) > averageForceReadingsRH.get(i))
            {
                increase++;
            } else if (averageForceReadingsRH.get(i + 1) < averageForceReadingsRH.get(i))
            {
                increase--;
            }
        }

        if (increase >= 3)
        {
            forceReadingsRH.clear();
            averageForceReadingsRH.clear();

            return true;
        }
        else
            return false;
    }

    public boolean isTurningPointDownRH()
    {
        int increase = 0;

        for (int i = 0; i < (averageForceReadingsRH.size() - 1); i++)
        {
            if (averageForceReadingsRH.get(i + 1) < averageForceReadingsRH.get(i))
            {
                increase++;
            } else if (averageForceReadingsRH.get(i + 1) > averageForceReadingsRH.get(i))
            {
                increase--;
            }
        }

        if (increase >= 3)
        {
            forceReadingsRH.clear();
            averageForceReadingsRH.clear();

            return true;
        }
        else
            return false;
    }

    public boolean isTurningPointUpRF()
    {
        int increase = 0;

        for (int i = 0; i < (averageForceReadingsRF.size() - 1); i++)
        {
            if (averageForceReadingsRF.get(i + 1) > averageForceReadingsRF.get(i))
            {
                increase++;
            } else if (averageForceReadingsRF.get(i + 1) < averageForceReadingsRF.get(i))
            {
                increase--;
            }
        }

        if (increase >= 3)
        {
            forceReadingsRF.clear();
            averageForceReadingsRF.clear();

            return true;
        }
        else
            return false;
    }

    public boolean isTurningPointDownRF()
    {
        int increase = 0;

        for (int i = 0; i < (averageForceReadingsRF.size() - 1); i++)
        {
            if (averageForceReadingsRF.get(i + 1) < averageForceReadingsRF.get(i))
            {
                increase++;
            } else if (averageForceReadingsRF.get(i + 1) > averageForceReadingsRF.get(i))
            {
                increase--;
            }
        }

        if (increase >= 3)
        {
            forceReadingsRF.clear();
            averageForceReadingsRF.clear();

            return true;
        }
        else
            return false;
    }

    public List<Float> getForceReadingsLH()
    {
        return forceReadingsLH;
    }

    public void setForceReadingsLH(List<Float> forceReadingsLH)
    {
        this.forceReadingsLH = forceReadingsLH;
    }

    public List<Float> getForceReadingsLF()
    {
        return forceReadingsLF;
    }

    public void setForceReadingsLF(List<Float> forceReadingsLF)
    {
        this.forceReadingsLF = forceReadingsLF;
    }

    public List<Float> getForceReadingsRH()
    {
        return forceReadingsRH;
    }

    public void setForceReadingsRH(List<Float> forceReadingsRH)
    {
        this.forceReadingsRH = forceReadingsRH;
    }

    public List<Float> getForceReadingsRF()
    {
        return forceReadingsRF;
    }

    public void setForceReadingsRF(List<Float> forceReadingsRF)
    {
        this.forceReadingsRF = forceReadingsRF;
    }

    public void setCurrentForceReadingLH(float currentForceReadingLH)
    {
        this.currentForceReadingLH = currentForceReadingLH;
        this.forceReadingsLH.add(currentForceReadingLH);

        this.averageForceReadingsLH.add(MathUtility.getMean(forceReadingsLH));
    }

    public void setCurrentForceReadingLF(float currentForceReadingLF)
    {
        this.currentForceReadingLF = currentForceReadingLF;
        this.forceReadingsLF.add(currentForceReadingLF);

        this.averageForceReadingsLF.add(MathUtility.getMean(forceReadingsLF));
    }

    public void setCurrentForceReadingRH(float currentForceReadingRH)
    {
        this.currentForceReadingRH = currentForceReadingRH;
        this.forceReadingsRH.add(currentForceReadingRH);

        this.averageForceReadingsRH.add(MathUtility.getMean(forceReadingsRH));
    }

    public void setCurrentForceReadingRF(float currentForceReadingRF)
    {
        this.currentForceReadingRF = currentForceReadingRF;

        this.forceReadingsRF.add(currentForceReadingRF);

        this.averageForceReadingsRF.add(MathUtility.getMean(forceReadingsRF));
    }
}
