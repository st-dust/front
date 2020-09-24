package cz.zatisigroup;

import com.vaadin.flow.component.charts.model.Background;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextAreaVariant;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;
import cz.zatisigroup.model.User;
import cz.zatisigroup.service.GetInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import org.springframework.context.annotation.Scope;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.EntityNotFoundException;
import java.util.Optional;

import static cz.zatisigroup.utills.ConvertToNumeric.*;

@RestController
@Scope("request")
@Route("")
@CssImport(value = "./styles/shared-styles.css")
@CssImport(value = "./styles/vaadin-text-field-styles.css", themeFor = "vaadin-text-field")
@Theme(value = Lumo.class, variant = Lumo.DARK)

public class MainView extends VerticalLayout {
    Logger log = LoggerFactory.getLogger(MainView.class);

    public MainView(@Autowired GetInfoService service) {
        TextField textField = new TextField("ID ke kontrole");
        textField.setAutofocus(true);

        textField.setPlaceholder("Sem vložte ID");
//        textField.setAutoselect(true);
        textField.setClearButtonVisible(true);

        Grid<User> grid = new Grid<>();
        TextArea successMessage = new TextArea();
        successMessage.setClassName("success-message");

        grid.addColumn(User::getId).setHeader("ID");
        grid.addColumn(User::getPersonalNumber).setHeader("Osobní číslo");
        grid.addColumn(User::getName).setHeader("Jméno");
        grid.addColumn(User::getSurname).setHeader("Příjmeni");
        grid.addColumn(User::getDepartment).setHeader("Středisko");
        grid.addColumn(User::getDepartmentID).setHeader("ID střediska");

        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER, GridVariant.LUMO_NO_ROW_BORDERS, GridVariant.LUMO_ROW_STRIPES);

        textField.setClassName("centered-content-button-textfield");

        grid.setClassName("v-grid");
        setClassName("centered-content");

        Button button = new Button("Ověřit ID",
                (e -> {
                    remove(grid, successMessage);

                    Optional<Integer> id = getNumber(textField.getValue());
                    if(id.isPresent()) {
                        try {
                            int textFieldIntValue = id.get();

                            User user = service.getUserById(textFieldIntValue);
                            grid.setItems(user);

                            successMessage.setValue(user.getName() + " " + user.getSurname() + " je zaměstnán/a v ZCG");
                            textField.setValue("");
                            add(successMessage, grid);

                        }catch (EntityNotFoundException ex) {
                            log.debug("ID {} není v DB", textField.getValue());

                            textField.setInvalid(true);
                            textField.setErrorMessage("Identifikátor nenalezen. Není nárok na slevu");
                        }
                    } else {
                        log.debug("ID {} není číslo", textField.getValue());

                        textField.setInvalid(true);
                        textField.setErrorMessage("Identifikátor nenalezen. Není nárok na slevu");
                    }
                }));

        button.setClassName("centered-content-button-textfield");
        button.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        button.addClickShortcut(Key.ENTER);

        add(textField, button);
    }
}
