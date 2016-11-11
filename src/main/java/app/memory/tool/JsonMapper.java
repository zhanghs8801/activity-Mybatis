/**
 * 
 */
package app.memory.tool;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser.Feature;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.JSONPObject;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationModule;

/**
 * 
 * 简单封装Jackson，实现JSON String<->Java Object的Mapper.
 * 封装不同的输出风格, 使用不同的builder函数创建实例.
 *
 */
public class JsonMapper {

	private static Logger logger = Logger.getLogger(JsonMapper.class);

	private ObjectMapper mapper;

	public JsonMapper() {
		super();
	}

	public JsonMapper(Include include) {
		this.mapper = new ObjectMapper();
		// 设置输出时包含属性的风格
		if (include != null) {
			this.mapper.setSerializationInclusion(include);
		}
		// 设置输入时忽略在JSON字符串中存在但Java对象实际没有的属性
		// this.mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
		// 设置默认DateFormat yyyy-MM-dd HH:mm:ss
		this.mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
	}

	/**
	 * 只输出非Null且非Empty(如List.isEmpty)的属性到Json字符串的Mapper,建议在外部接口中使用.
	 */
	public static JsonMapper nonEmptyMapper() {
		return new JsonMapper(Include.NON_EMPTY);
	}

	/**
	 * 创建只输出初始值被改变的属性到Json字符串的Mapper, 最节约的存储方式，建议在内部接口中使用。
	 */
	public static JsonMapper nonDefaultMapper() {
		return new JsonMapper(Include.NON_DEFAULT);
	}

	public static JsonMapper alwaysMapper() {
		return new JsonMapper(Include.ALWAYS);
	}

	/**
	 * 允许单引号 允许不带引号的字段名称
	 */
	public JsonMapper enableSimple() {
		mapper.configure(Feature.ALLOW_SINGLE_QUOTES, true);
		mapper.configure(Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
		return this;
	}

	public static JsonMapper getInstance() {
		return alwaysMapper();
	}

	/**
	 * Object可以是POJO，也可以是Collection或数组。 如果对象为Null, 返回"null". 如果集合为空集合, 返回"[]".
	 */
	public String toJson(Object object) {
		try {
			return mapper.writeValueAsString(object);
		} catch (IOException e) {
			logger.warn("write to json string error:" + object, e);
			return null;
		}
	}

	/**
	 * 反序列化POJO或简单Collection如List<String>.
	 * 
	 * 如果JSON字符串为Null或"null"字符串, 返回Null. 如果JSON字符串为"[]", 返回空集合.
	 * 
	 * 如需反序列化复杂Collection如List<MyBean>, 请使用fromJson(String,JavaType)
	 * 
	 * @see #fromJson(String, JavaType)
	 */
	public <T> T fromJson(String jsonString, Class<T> clazz) {
		if (StringUtils.isEmpty(jsonString)) {
			return null;
		}

		try {
			return mapper.readValue(jsonString, clazz);
		} catch (IOException e) {
			logger.warn("parse json string error:" + jsonString, e);
			return null;
		}
	}

	/**
	 * 反序列化复杂Collection如List<Bean>, 先使用函数createCollectionType构造类型,然后调用本函数.
	 * 
	 * @see #createCollectionType(Class, Class...)
	 */
	@SuppressWarnings("unchecked")
	public <T> T fromJson(String jsonString, JavaType javaType) {
		if (StringUtils.isEmpty(jsonString)) {
			return null;
		}

		try {
			return (T) mapper.readValue(jsonString, javaType);
		} catch (IOException e) {
			logger.warn("parse json string error:" + jsonString, e);
			return null;
		}
	}

	/**
	 * 构造泛型的Collection Type如: ArrayList<MyBean>,
	 * 则调用constructCollectionType(ArrayList.class,MyBean.class)
	 * HashMap<String,MyBean>, 则调用(HashMap.class,String.class, MyBean.class)
	 */
	public JavaType createCollectionType(Class<?> collectionClass, Class<?>... elementClasses) {
		return mapper.getTypeFactory().constructParametricType(collectionClass, elementClasses);
	}

	/**
	 * 当JSON里只含有Bean的部分属性时，更新一个已存在Bean，只覆盖该部分属性.
	 */
	@SuppressWarnings("unchecked")
	public <T> T update(String jsonString, T object) {
		try {
			return (T) mapper.readerForUpdating(object).readValue(jsonString);
		} catch (JsonProcessingException e) {
			logger.warn("update json string:" + jsonString + " to object:" + object + " error.", e);
		} catch (IOException e) {
			logger.warn("update json string:" + jsonString + " to object:" + object + " error.", e);
		}
		return null;
	}

	/**
	 * 输出JSONP格式数据
	 */
	public String toJsonP(String functionName, Object object) {
		return toJson(new JSONPObject(functionName, object));
	}

	/**
	 * 设定是否使用Enum的toString函数来读写Enum, 为False时使用Enum的name()函数来读写Enum, 默认为False.
	 * 注意本函数一定要在Mapper创建后, 所有的读写动作之前调用.
	 */
	public JsonMapper enableEnumUseToString() {
		mapper.enable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING);
		mapper.enable(DeserializationFeature.READ_ENUMS_USING_TO_STRING);
		return this;
	}

	/**
	 * 支持使用Jaxb的Annotation，使得POJO上的annotation不用与Jackson耦合。
	 * 默认会先查找jaxb的annotation，如果找不到再找jackson的。
	 */
	public JsonMapper enableJaxbAnnotation() {
		JaxbAnnotationModule module = new JaxbAnnotationModule();
		this.mapper.registerModule(module);
		return this;
	}

	/**
	 * 设定格式化Date类型数据格式 在所有读写动作之前调用
	 */
	public void setDateFormat(String dateFormat) {
		this.mapper.setDateFormat(new SimpleDateFormat(dateFormat));
	}

	/**
	 * 取出Mapper做进一步的设置或使用其他序列化API.
	 */
	public ObjectMapper getMapper() {
		return mapper;
	}

	/**
	 * 
	 * @param jsonList
	 * @param class1
	 * @return
	 */
	public <K> List<K> fromJsonList(List<String> jsonList, Class<K> clz) {
		List<K> res = new ArrayList<K>();
		for (int k=0;k<jsonList.size();k++) {
			res.add(fromJson(jsonList.get(k), clz));
		}
		return res;
	}

	@SuppressWarnings("unchecked")
	public <T> List<T> toBeanList(String json,Class<T> clz) throws JsonParseException, JsonMappingException, IOException{
		JavaType javaType=mapper.getTypeFactory().constructParametricType(ArrayList.class, clz);
		return  (List<T>)mapper.readValue(json, javaType); 
	}
}
