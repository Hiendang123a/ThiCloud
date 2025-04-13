package com.example.society.Repository;
import com.example.society.Entity.Message;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Query;
import java.util.Date;
import java.util.List;

public interface IMessageRepository extends MongoRepository<Message, ObjectId> {
    @Query(value = "{'$or': [{'senderID': ?0, 'receiverID': ?1}, {'senderID': ?1, 'receiverID': ?0}], 'sentAt': {'$lt': ?2}}", sort = "{'sentAt': -1}")
    List<Message> findMessagesBetweenUsers(ObjectId senderID, ObjectId receiverID, Date cursor, Pageable pageable);

    @Query(value = "{ 'senderID': ?0, 'receiverID': ?1, 'seenAt': null }")
    List<Message> findUnseenMessages(ObjectId receiverID, ObjectId senderID);

    @Aggregation(pipeline = {
            "{ '$match': { '$or': [ { 'senderID': ?0 }, { 'receiverID': ?0 } ] } }", // Lọc tin nhắn mà bạn tham gia
            "{ '$sort': { 'sentAt': -1 } }", // Sắp xếp giảm dần theo thời gian gửi
            "{ '$group': { '_id': { '$cond': { 'if': { '$gt': ['$senderID', '$receiverID'] }, 'then': ['$senderID', '$receiverID'], 'else': ['$receiverID', '$senderID'] } }, 'lastMessage': { '$first': '$$ROOT' } } }",
            "{ '$replaceRoot': { 'newRoot': '$lastMessage' } }" // Chuyển đổi kết quả nhóm lại thành các tin nhắn
    })
    List<Message> findLastMessages(ObjectId userId);
}
