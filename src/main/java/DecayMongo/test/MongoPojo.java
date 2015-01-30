package DecayMongo.test;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import DecayMongo.EntityDefinition;
import DecayMongo.MongoConnection;
import DecayMongo.annotations.Entity;
import DecayMongo.annotations.GroupEmbed;
import DecayMongo.annotations.ID;
import DecayMongo.cache.DaosCache;
import DecayMongo.dao.ToolDao;

import com.mongodb.WriteResult;

/**
 * 2014年11月21日
 * @author decaywood
 *
 */
 
@Entity(name = "deca")
public class MongoPojo implements EntityDefinition{
    
    @ID
    private String id = "1114";
    @GroupEmbed(groupClass = ArrayList.class, name = "wocao ni ma")
    private List<Date> list = new ArrayList<>();
    
    @GroupEmbed(groupClass = LinkedList.class, name = "referenced")
    private List<Double> linkedList = new LinkedList<>();
    /**
     * 
     */
    public MongoPojo() {
//        list.add(1);
//        list.add(2);
//        list.add(3);
        list.add(new Date());
        list.add(new Date());
        list.add(new Date());
        linkedList.add(1.2321);
        linkedList.add(2.44);
        linkedList.add(5.3412);
    }
    
    public static void main(String[] args) {
        MongoConnection connection = MongoConnection.getInstance();
        connection.startConnect();
        DaosCache<MongoPojo> cache = DaosCache.getInstance();
        ToolDao<MongoPojo> dao = cache.get(MongoPojo.class);
        WriteResult result = dao.createData(new MongoPojo());
        Object object = result.getUpsertedId();
        MongoPojo pojo = dao.findOne("546f56c9deddd8e0ad4ea16e");
        MongoPojo pojo1 = dao.findOne(object.toString());
    }

    /**
     * 2014年11月21日
     * @author decaywood
     *
     */ 
    @Override
    public void setID(String id) {
        this.id = id;
    }

    /**
     * 2014年11月21日
     * @author decaywood
     *
     */ 
    @Override
    public String getID() {
        return id;
    }
 
    

}
