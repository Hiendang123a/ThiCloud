package com.example.society.Mapper;

import org.bson.types.ObjectId;
import org.mapstruct.Mapper;
import org.mapstruct.Named;

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
}
