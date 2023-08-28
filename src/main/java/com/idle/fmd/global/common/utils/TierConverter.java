package com.idle.fmd.global.common.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class TierConverter {
    public int soloTierToNumber(String tier){
        Integer tierCode;

        switch (tier){
            case "UNRANKED": tierCode = 0; break;
            case "IRON": tierCode = 1; break;
            case "BRONZE": tierCode = 2; break;
            case "SILVER": tierCode = 3; break;
            case "GOLD": tierCode = 4; break;
            case "PLATINUM": tierCode = 5; break;
            case "EMERALD": tierCode = 6; break;
            case "DIAMOND": tierCode = 7; break;
            default: tierCode = -1;
        }
        return tierCode;
    }

    public int flexTierToNumber(String tier){
        Integer tierCode = 0;

        switch (tier){
            case "UNRANKED": tierCode = 0; break;
            case "IRON": tierCode = 1; break;
            case "BRONZE": tierCode = 2; break;
            case "SILVER": tierCode = 3; break;
            case "GOLD": tierCode = 4; break;
            case "PLATINUM": tierCode = 5; break;
            case "EMERALD": tierCode = 6; break;
            case "DIAMOND": tierCode = 7; break;
            case "MASTER": tierCode = 8; break;
            case "GRANDMASTER": tierCode = 9; break;
            case "CHALLENGER": tierCode = 10; break;
        }
        return tierCode;
    }

//    public String tierToString(Integer tier){
//        String tierName;
//
//        switch (tier){
//            case 0: tierName = "UNRANKED"; break;
//            case 1: tierName = "IRON"; break;
//            case 2: tierName = "BRONZE"; break;
//            case 3: tierName = "SILVER"; break;
//            case 4: tierName = "GOLD"; break;
//            case 5: tierName = "PLATINUM"; break;
//            case 6: tierName = "EMERALD"; break;
//            case 7: tierName = "DIAMOND"; break;
//            default: tierName = "ERROR";
//        }
//
//        return tierName;
//    }

    public boolean soloTierInRange(String myTier, String duoTier){
        int myTierInt = soloTierToNumber(myTier);
        int duoTierInt = soloTierToNumber(duoTier);

        log.info("{}",myTierInt);
        log.info("{}",duoTierInt);

        if(myTierInt <= 3){
            return (0 <= duoTierInt && duoTierInt <= 3) || duoTierInt <= myTierInt + 1;
        }
        else{
            return myTierInt -1 <= duoTierInt  && duoTierInt <= myTierInt + 1;
        }
    }

    public boolean flexTierInRange(String myTier, String duoTier){
        int myTierInt = flexTierToNumber(myTier);
        int duoTierInt = flexTierToNumber(duoTier);

        if(myTierInt >= 8) return duoTierInt >= 6;
        if(duoTierInt >= 8) return myTierInt >= 6;

        return true;
    }
}
