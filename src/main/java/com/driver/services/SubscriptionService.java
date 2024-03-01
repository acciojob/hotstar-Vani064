package com.driver.services;


import com.driver.EntryDto.SubscriptionEntryDto;
import com.driver.model.Subscription;
import com.driver.model.SubscriptionType;
import com.driver.model.User;
import com.driver.repository.SubscriptionRepository;
import com.driver.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class SubscriptionService {

    @Autowired
    SubscriptionRepository subscriptionRepository;

    @Autowired
    UserRepository userRepository;

    public Integer buySubscription(SubscriptionEntryDto subscriptionEntryDto){

        //Save The subscription Object into the Db and return the total Amount that user has to pay
        Subscription subscription = new Subscription();
        User user = userRepository.findById(subscriptionEntryDto.getUserId()).get();

        String s = subscriptionEntryDto.getSubscriptionType().toString();
        int amount =0;
        if(s.equalsIgnoreCase("basic"))
        {
            subscription.setSubscriptionType(SubscriptionType.BASIC);
            amount = 500 + (200*subscriptionEntryDto.getNoOfScreensRequired());
            subscription.setTotalAmountPaid(amount);
        }
        else if(s.equalsIgnoreCase("pro"))
        {
            subscription.setSubscriptionType(SubscriptionType.PRO);
            amount = 800 + (250*subscriptionEntryDto.getNoOfScreensRequired());
            subscription.setTotalAmountPaid(amount);
        }
        else if(s.equalsIgnoreCase("elite"))
        {
            subscription.setSubscriptionType(SubscriptionType.ELITE);
            amount = 1000 + (350*subscriptionEntryDto.getNoOfScreensRequired());
            subscription.setTotalAmountPaid(amount);
        }

        subscription.setStartSubscriptionDate(new Date());
        subscription.setUser(user);
        subscription.setNoOfScreensSubscribed(subscriptionEntryDto.getNoOfScreensRequired());

        user.setSubscription(subscription);
        userRepository.save(user);
        return amount;
    }

    public Integer upgradeSubscription(Integer userId)throws Exception{

        //If you are already at an ElITE subscription : then throw Exception ("Already the best Subscription")
        //In all other cases just try to upgrade the subscription and tell the difference of price that user has to pay
        //update the subscription in the repository

        User user = userRepository.findById(userId).get();
        Subscription subscription = user.getSubscription();
        int amountdiff =0;

        if(subscription.getSubscriptionType() == SubscriptionType.ELITE)
            throw new Exception("Already the best Subscription");
        else if(subscription.getSubscriptionType() == SubscriptionType.PRO)
        {
             subscription.setSubscriptionType(SubscriptionType.ELITE);
             //elite - already paid
             amountdiff = (1000 +(350*subscription.getNoOfScreensSubscribed()))- subscription.getTotalAmountPaid();
             subscription.setTotalAmountPaid(1000 +(350*subscription.getNoOfScreensSubscribed()));
             subscription.setStartSubscriptionDate(new Date());
        }
        else if(subscription.getSubscriptionType()== SubscriptionType.BASIC)
        {
            subscription.setSubscriptionType(SubscriptionType.PRO);
            //pro - already paid
            amountdiff = (800 +(250*subscription.getNoOfScreensSubscribed())) - subscription.getTotalAmountPaid();
            subscription.setTotalAmountPaid(800+(250*subscription.getNoOfScreensSubscribed()));
            subscription.setStartSubscriptionDate(new Date());
        }
        subscriptionRepository.save(subscription);
        return amountdiff;
    }

    public Integer calculateTotalRevenueOfHotstar(){

        //We need to find out total Revenue of hotstar : from all the subscriptions combined
        //Hint is to use findAll function from the SubscriptionDb
         List<Subscription> subscriptionList = subscriptionRepository.findAll();
         int revenue =0;
         for(Subscription subscription : subscriptionList)
         {
             revenue += subscription.getTotalAmountPaid();
         }
        return revenue;
    }

}
