package net.omega2097;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import org.lwjgl.util.vector.Vector3f;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class MapLoader {

    public List<GameObject> load(String fileName) {
        List<GameObject> gameObjectList = new ArrayList<>();

        if (!Files.exists(Paths.get(fileName))) {
            throw new RuntimeException("File does not exist " + fileName);
        }
        try {
            byte[] encoded = Files.readAllBytes(Paths.get(fileName));
            String mapString = new String(encoded, "utf-8");
            JsonObject mapData = Json.parse(mapString).asObject();
            JsonArray objects = mapData.get("objects").asArray();

            for (JsonValue value : objects) {
                JsonObject jsonObj = value.asObject();
                JsonArray jPos = jsonObj.get("position").asArray();
                JsonArray jRot = jsonObj.get("rotation").asArray();
                JsonArray jScale = jsonObj.get("scale").asArray();
                String modelName = jsonObj.get("model").asString();
                String textureName = jsonObj.get("texture").asString();

                Vector3f position = new Vector3f(jPos.get(0).asFloat(), jPos.get(1).asFloat(), jPos.get(2).asFloat());
                Vector3f rotation = new Vector3f(jRot.get(0).asFloat(),jRot.get(1).asFloat(),jRot.get(2).asFloat());
                Vector3f scale = new Vector3f(jScale.get(0).asFloat(),jScale.get(1).asFloat(),jScale.get(2).asFloat());

                GameObject gameObject = new GameObject();
                gameObject.setModelName(modelName);
                gameObject.setTextureName(textureName);
                gameObject.setPosition(position.x, position.y, position.z);
                gameObject.setRotation(rotation.x, rotation.y, rotation.z);
                gameObject.setScale(scale.x, scale.y, scale.z);
                gameObjectList.add(gameObject);
            }
        } catch (IOException e) {
            throw new RuntimeException("Error reading file to string" + fileName);
        }

        return gameObjectList;
    }
}
