package com.capstone.moayo.service.concrete;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.capstone.moayo.dao.mapping.CategoryMapping;
import com.capstone.moayo.dao.mapping.DogamMapping;
import com.capstone.moayo.entity.Category;
import com.capstone.moayo.entity.CategoryNode;
import com.capstone.moayo.service.CategoryService;
import com.capstone.moayo.service.dto.CategoryDto;
import com.capstone.moayo.service.dto.CategoryNodeDto;
import com.capstone.moayo.storage.CategoryStorage;
import com.capstone.moayo.storage.DogamStorage;
import com.capstone.moayo.storage.concrete.StorageFactoryCreator;
import com.capstone.moayo.util.DogamStatus;
import com.capstone.moayo.util.Exception.MutableException;
import com.capstone.moayo.util.Exception.NoSuchCategoryException;
import com.capstone.moayo.util.Exception.NoSuchNodeException;
import com.capstone.moayo.util.Exception.NotRootException;
import com.capstone.moayo.util.Exception.NullCategoryException;
import com.capstone.moayo.util.Exception.NullRootException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ConcreteCategoryService implements CategoryService {
    private CategoryStorage categoryStorage;
    private DogamStorage dogamStorage;
    private Context applicationContext;


    public ConcreteCategoryService(Context context) {
        this.categoryStorage = StorageFactoryCreator.getInstance().requestCategoryStorage(context);
        this.dogamStorage = StorageFactoryCreator.getInstance().requestDogamStorage(context);
        applicationContext = context;
    }

    @Override
    public String createCategory(CategoryDto newCategoryDto){
        Log.d("create category", newCategoryDto.toString());
        try {

            if (newCategoryDto.getRootNode() == null)
                throw  new NullRootException("There is no root category node");
            if (newCategoryDto.getRootNode().getLevel() != 1)
                throw new NotRootException("This is not a root node");

            Category newCategory = newCategoryDto.toCategory();

            int dogamId = dogamStorage.create(newCategory);
            newCategory.setId(dogamId);
            String result = categoryStorage.create(newCategory);
            return result;
        } catch (NullRootException | NotRootException e) {
            Log.d("error found in service", e.toString());
        }

        return null;
    }

    @Override
    public List<CategoryDto> findAll() {
        try {
            Collection<Category> categories = dogamStorage.retrieveAll();
            if (categories == null)
                throw new  NoSuchCategoryException("You don't have any category now");

            for (Category category : categories) {
                CategoryNode categoryNode = categoryStorage.retrieveByDogamId(category.getId());
                if(categoryNode == null)
                    throw new NoSuchNodeException("There is no such node");
                if(categoryNode.getParent() != null)
                    throw new NotRootException("Not root node");
                category.setRootNode(categoryNode);
            }

            List<CategoryDto> categoryDtoList = new ArrayList<>();
            for (Category category : categories) {
                CategoryDto categoryDto = category.toCategoryDto();
                categoryDtoList.add(categoryDto);
            }
            Log.d("found", categoryDtoList.toString());
            return categoryDtoList;
        } catch (NoSuchCategoryException | NoSuchNodeException | NotRootException e) {
            Log.d("error in service",e.toString());
        }

        return null;
    }

    @Override
    public CategoryDto findCategoryById(int id) {
        CategoryDto foundCategoryDto = null;
        try {
            DogamMapping foundDogam = dogamStorage.retrieveById(id);
            if(foundDogam == null)
                throw new NoSuchCategoryException("There is no such category");

            CategoryNode rootNode = categoryStorage.retrieveByDogamId(foundDogam.getId());
            if(rootNode == null)
                throw new NoSuchNodeException("there is no such node");

            Category foundCategory = new Category(foundDogam.getTitle(), foundDogam.getDesription(), foundDogam.getPassword(), rootNode);
            foundCategory.setId(foundDogam.getId());
            foundCategoryDto = foundCategory.toCategoryDto();
        } catch (NoSuchCategoryException | NoSuchNodeException e) {
            Toast.makeText(applicationContext, e.toString(), Toast.LENGTH_SHORT).show();
        }

        return foundCategoryDto;
    }

    @Override
    public String modifyCategory(CategoryDto categoryDto) {
        Category modifyCategory = categoryDto.toCategory();
        try {
            if(modifyCategory.getStatus() == DogamStatus.Shared_Mutable)
                throw new MutableException("can not modify");

            DogamMapping foundDogam = dogamStorage.retrieveById(modifyCategory.getId());
            if (foundDogam == null)
                throw new NoSuchCategoryException("there is no such category");

            dogamStorage.update(modifyCategory);
            String result = categoryStorage.update(modifyCategory);
            initCache(categoryDto);
            return result;
        } catch (NoSuchNodeException | MutableException e) {
            Toast.makeText(applicationContext, e.toString(), Toast.LENGTH_SHORT).show();
        }

        return null;
    }

    @Override
    public String deleteDogam(int id) {
        String result = "";
        try {
            DogamMapping foundDogam = dogamStorage.retrieveById(id);
            if(foundDogam == null)
                throw new NullCategoryException();
            boolean re = dogamStorage.remove(foundDogam.getId());
            if(re != true) {
                result = "fail to delete dogam";
            } else {
                result = "success to delete dogam";
            }
        } catch (NullCategoryException e) {
            Toast.makeText(applicationContext, e.toString(), Toast.LENGTH_SHORT).show();
        }

        return result;
    }

    @Override
    public String deleteCategoryNode(int dogamId, int id) {
        String result = "";
        try {
            CategoryNode foundNode = categoryStorage.retrieveById(dogamId, id);
            if(foundNode == null) {
                throw new Exception();
            }
            result = categoryStorage.remove(dogamId, id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private void initCache(CategoryDto categoryDto) {
        CategoryNodeDto rootNode = categoryDto.getRootNode();
        rootNode.getCache().clear();
        for(CategoryNodeDto secondNode : rootNode.getLowLayer()) {
            secondNode.getCache().clear();
            for(CategoryNodeDto thirdNode : secondNode.getLowLayer()) {
                thirdNode.getCache().clear();
            }
        }
    }
}
