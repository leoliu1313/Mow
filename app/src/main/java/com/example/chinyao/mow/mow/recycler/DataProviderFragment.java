/*
 *    Copyright (C) 2015 Haruki Hasegawa
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.example.chinyao.mow.mow.recycler;


import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.example.chinyao.mow.mow.model.DataProvider;
import com.example.chinyao.mow.mow.model.TodoModel;

import java.util.ArrayList;

public class DataProviderFragment extends Fragment {
    private DataProvider mDataProvider;
    private ArrayList<TodoModel> itemsArrayList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // orientation issue
        setRetainInstance(true);  // keep the mDataProvider instance

        mDataProvider = new DataProvider();
        mDataProvider.addItems(itemsArrayList);
    }

    public DataProvider getDataProvider() {
        return mDataProvider;
    }

    public DataProviderFragment addItems(ArrayList<TodoModel> itemsArrayList) {
        this.itemsArrayList = itemsArrayList;
        return this;
    }
}
