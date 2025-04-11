package com.example.society.Mapper;

import org.bson.types.ObjectId;
import org.mapstruct.Mapper;
import org.mapstruct.Named;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface ConverterMapper {

    @Named("stringToObjectID")
    default ObjectId stringToObjectID(String id){
        return new ObjectId(id);
    }

    @Named("objectIDToString")
    default String objectIDToString(ObjectId id){
        return id.toString();
    }


    @Named("listObjectIDToListString")
    default List<String> listObjectIDToListString(List<ObjectId> ids) {
        if (ids == null)
            return null;
        List<String> result = new ArrayList<>();
        for (ObjectId id : ids) {
            result.add(id.toString());
        }
        return result;
    }

    @Named("listStringToListObjectID")
    default List<ObjectId> listStringToListObjectID(List<String> strings) {
        if (strings == null)
            return null;
        List<ObjectId> result = new ArrayList<>();
        for (String string : strings) {
            result.add(new ObjectId(string));
        }
        return result;
    }
}
