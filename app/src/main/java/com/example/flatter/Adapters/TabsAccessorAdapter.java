package com.example.flatter.Adapters;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.flatter.Fragments.ChatsFragment;
import com.example.flatter.Fragments.ContactsFragment;
import com.example.flatter.Fragments.GroupsFragment;
import com.example.flatter.Fragments.RequestFragment;


/**
 * Created by Aditya Prakash on 22-05-2019.
 */

public class TabsAccessorAdapter extends FragmentPagerAdapter  {
    public TabsAccessorAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
        switch(i){
            case 0:
                ChatsFragment chatsFragment = new ChatsFragment();
                return chatsFragment;
            case 1:
                ContactsFragment contactsFragment = new ContactsFragment();
                return contactsFragment;
            case 2:
                RequestFragment requestFragment = new RequestFragment();
                return requestFragment;
//            case 3:
//                GroupsFragment groupsFragment = new GroupsFragment();
//                return groupsFragment;

            default:
                return null;
        }

    }

    @Override
    public int getCount() {
        return 3;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {


        switch(position){
            case 0:
                return "Chats";
            case 1:
                return "Contacts";
            case 2:
                return "Requests";
//            case 3:
//                return "Groups";
            default:
                return null;
        }
    }
}

