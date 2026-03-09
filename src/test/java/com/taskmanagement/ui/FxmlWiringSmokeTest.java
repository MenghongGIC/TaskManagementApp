package com.taskmanagement.ui;

import org.junit.jupiter.api.Test;

import javax.xml.parsers.DocumentBuilderFactory;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;

class FxmlWiringSmokeTest {

    private static final Path RESOURCES_ROOT = Path.of("src/main/resources");

    private static final List<String> TARGET_FXMLS = List.of(
        "/com/taskmanagement/fxml/main/Dashboard.fxml",
        "/com/taskmanagement/fxml/task/TaskList.fxml",
        "/com/taskmanagement/fxml/main/KanbanBoard.fxml",
        "/com/taskmanagement/fxml/project/ProjectList.fxml",
        "/com/taskmanagement/fxml/team/TeamListView.fxml",
        "/com/taskmanagement/fxml/main/ActivityView.fxml",
        "/com/taskmanagement/fxml/dialog/TaskForm.fxml",
        "/com/taskmanagement/fxml/dialog/ProjectForm.fxml"
    );

    @Test
    void targetFxmlFilesShouldExistAndBeParsable() throws Exception {
        List<String> failures = new ArrayList<>();
        for (String fxmlPath : TARGET_FXMLS) {
            Path filePath = RESOURCES_ROOT.resolve(fxmlPath.substring(1));
            if (!Files.exists(filePath)) {
                failures.add("Missing FXML: " + fxmlPath);
                continue;
            }
            try {
                var parser = DocumentBuilderFactory.newInstance();
                parser.setNamespaceAware(true);
                parser.newDocumentBuilder().parse(filePath.toFile());
            } catch (Exception ex) {
                failures.add("Unparsable FXML: " + fxmlPath + " -> " + ex.getMessage());
            }
        }
        assertTrue(failures.isEmpty(), String.join("\n", failures));
    }

    @Test
    void targetFxmlOnActionHandlersShouldExistOnControllers() throws Exception {
        List<String> failures = new ArrayList<>();

        for (String fxmlPath : TARGET_FXMLS) {
            Path filePath = RESOURCES_ROOT.resolve(fxmlPath.substring(1));
            if (!Files.exists(filePath)) {
                failures.add("Missing FXML: " + fxmlPath);
                continue;
            }

            var parser = DocumentBuilderFactory.newInstance();
            parser.setNamespaceAware(true);
            var document = parser.newDocumentBuilder().parse(filePath.toFile());
            var root = document.getDocumentElement();

            String controllerName = root.getAttributeNS("http://javafx.com/fxml/1", "controller");
            if (controllerName == null || controllerName.isBlank()) {
                continue;
            }

            Class<?> controllerClass;
            try {
                controllerClass = Class.forName(controllerName);
            } catch (ClassNotFoundException ex) {
                failures.add("Controller class not found for " + fxmlPath + ": " + controllerName);
                continue;
            }

            Set<String> onActionHandlers = collectOnActionHandlers(document.getDocumentElement());
            for (String handler : onActionHandlers) {
                if (!hasMethod(controllerClass, handler)) {
                    failures.add("Missing handler '#" + handler + "' in " + controllerName + " for " + fxmlPath);
                }
            }
        }

        assertTrue(failures.isEmpty(), String.join("\n", failures));
    }

    private static Set<String> collectOnActionHandlers(org.w3c.dom.Node node) {
        Set<String> handlers = new LinkedHashSet<>();
        collect(node, handlers);
        return handlers;
    }

    private static void collect(org.w3c.dom.Node node, Set<String> handlers) {
        if (node instanceof org.w3c.dom.Element element) {
            var attrs = element.getAttributes();
            for (int i = 0; i < attrs.getLength(); i++) {
                var attr = attrs.item(i);
                if ("onAction".equals(attr.getLocalName()) || "onAction".equals(attr.getNodeName())) {
                    String value = attr.getNodeValue();
                    if (value != null && value.startsWith("#") && value.length() > 1) {
                        handlers.add(value.substring(1));
                    }
                }
            }
        }

        var children = node.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            collect(children.item(i), handlers);
        }
    }

    private static boolean hasMethod(Class<?> type, String methodName) {
        for (Method method : type.getDeclaredMethods()) {
            if (method.getName().equals(methodName)) {
                return true;
            }
        }
        return false;
    }
}
