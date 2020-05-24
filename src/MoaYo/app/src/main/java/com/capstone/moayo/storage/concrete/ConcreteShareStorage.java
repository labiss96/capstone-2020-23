package com.capstone.moayo.storage.concrete;

import android.content.Context;
import android.util.Log;

import com.capstone.moayo.dao.CategoryDao;
import com.capstone.moayo.dao.DogamDao;
import com.capstone.moayo.dao.concrete.DaoFactoryCreator;
import com.capstone.moayo.entity.Model.DogamModel;
import com.capstone.moayo.entity.Model.ModelForm;
import com.capstone.moayo.storage.ShareStorage;
import com.capstone.moayo.util.retrofit.APIUtils;
import com.capstone.moayo.util.retrofit.ShareAPI;
import com.capstone.moayo.util.retrofit.ShareResponse;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class ConcreteShareStorage implements ShareStorage {
    private ShareAPI shareAPI;

    public ConcreteShareStorage(Context context) {
        this.shareAPI = APIUtils.getShareAPI();
    }

    @Override
    public int create(ModelForm form) {
        int result = 0;
        Call<ShareResponse> call = shareAPI.requestCreate(form);
        try {
            Response<ShareResponse> response = call.execute();
            result = response.body().getCode();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public ModelForm retrieveById(int id) {
        Call<ModelForm> call = shareAPI.requestDogam(id);
        try {
            Response<ModelForm> response = call.execute();
            ModelForm form = response.body();
            if(form != null)
                return form;
            else
                return null;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<DogamModel> retrieveAll() {
        Call<DogamModel[]> call = shareAPI.requestDogamAll();
        try {
            Response<DogamModel[]> response = call.execute();
            List<DogamModel> body = Arrays.asList(response.body());

            return body;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public int remove(int id) {
        int result = 1;
        Call<ShareResponse> call = shareAPI.requsetDelete(id);
        try {
            Response<ShareResponse> response = call.execute();
            result = response.body().getCode();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
}
