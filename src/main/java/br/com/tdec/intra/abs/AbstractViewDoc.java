package br.com.tdec.intra.abs;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.richtexteditor.RichTextEditor;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.UploadI18N;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.OptionalParameter;
import com.vaadin.flow.router.QueryParameters;
import com.vaadin.flow.server.StreamResource;

import br.com.tdec.intra.abs.AbstractModelDoc.RichText;
import br.com.tdec.intra.abs.AbstractService.DeleteResponse;
import br.com.tdec.intra.abs.AbstractService.SaveResponse;
import br.com.tdec.intra.config.MailService;
import br.com.tdec.intra.utils.converters.RichTextToMimeConverter;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@CssImport(value = "./themes/intra24/views/abstract-view-doc.css", themeFor = "vaadin-form-layout")
public abstract class AbstractViewDoc<T extends AbstractModelDoc> extends FormLayout
		implements HasUrlParameter<String> {

	private static final long serialVersionUID = 1L;
	protected T model;
	protected Class<T> modelType;
	protected AbstractService<T> service;
	protected MailService mailService;
	@Autowired
	private ObjectMapper objectMapper; // jackson conversao zonedDateTime
	protected String unid;
	protected Binder<T> binder;
	protected RichTextEditor obsField = new RichTextEditor(); // opcional na classe concreta
	protected List<Component> binderFields = new ArrayList<>();// imporante para manter a ordem dos campos no updateView
	protected boolean isNovo;
	protected boolean isReadOnly;
	protected Map<String, String> queryParams;
	protected H1 title;
	protected Button saveButton;
	protected Button cancelButton;
	protected Button editButton;
	protected Button deleteButton;
	protected Dialog deleteDialog;
	protected Set<HasValue<?, ?>> readOnlyFields = new HashSet<>();
	protected HorizontalLayout horizontalLayoutButtons;
	protected VerticalLayout verticalLayoutAnexos;
	protected MemoryBuffer buffer = new MemoryBuffer();
	protected boolean anexosCarregados; // para não carregar novamente ao clicar em edit
	protected Upload upload = new Upload(buffer);
	protected HorizontalLayout footer;
	protected Span autorSpan;
	protected Span criacaoSpan;
	protected Span idSpan;

	public AbstractViewDoc(Class<T> modelType, AbstractService<T> service) {
		this.service = service;
		this.modelType = modelType;
		binder = new Binder<>(modelType);

		this.setTitle(title);
		getStyle().set("flex-grow", "0");
		getStyle().set("margin-left", "10px");
		getStyle().set("margin-right", "10px");
		this.getStyle().set("padding-bottom", "60px"); // Ajuste a altura conforme necessário - footer

		// Ao clicar duas no form, entra em modo de edição
		this.addDoubleClickListener(event -> edit());
	}

	@Override
	public void setParameter(BeforeEvent event, @OptionalParameter String parameter) {
		this.unid = parameter;
		QueryParameters queryParameters = event.getLocation().getQueryParameters();
		Map<String, List<String>> parametersMap = queryParameters.getParameters();
		if (parameter == null || parameter.isEmpty()) {
			isNovo = true;
			model = createModel();
			model.init();
			isReadOnly = false;
		} else {
			// Se existe um parâmetro, tenta carregar o objeto existente
			isNovo = false;
			model = findByUnid(unid);
			isReadOnly = true;

			// Verifica se o sistema pediu para entrar como editável
			if (parametersMap.containsKey("isEditable") && parametersMap.get("isEditable").get(0).equals("1")) {
				isReadOnly = false;
			}
		}

		updateView();
		initFooter();

	}

	/**
	 * sempre que algum componente foi alterado, tenho que refazer toda a tela.
	 * Apenas adicinoar um componente em caso de edit, por exemplo, ira colocá-lo
	 * fora de ordem. Primeiro o Binder, depois os richtext, depois o upload, depois
	 * os files, depois os botoes e depois o foooter.
	 */
	public void updateView() {
		// Inicializa o Binder
		initBinder();

		// Verifica se o modelo possui o campo 'obs' e inicializa o campo de observações
		initObsFieldIfNeeded();

		// define o modelo

		binder.setBean(model);

		// Limpa o layout e adiciona os campos do Binder na ordem correta
		this.removeAll();
		binderFields.forEach(this::add);

		// Chama o método abstrato para adicionar componentes personalizados da
		// subclasse
		addCustomComponents();

		// Adiciona os anexos (upload e arquivos)
		initAnexos();

		// Adiciona os botões de ação
		initButtons();

		updateReadOnlyState();

	}

	/**
	 * Inicializa o campo de observações (RichTextEditor) se o modelo tiver o campo
	 * 'obs'.
	 */
	private void initObsFieldIfNeeded() {
		try {
			// Verifica se o campo 'obs' existe na classe do modelo
			if (model.getClass().getDeclaredField("obs") != null) {
				System.out.println("O campo 'obs' existe na classe do modelo");
				// Verifica se o campo 'obs' está nulo e inicializa com um objeto vazio
				model = modelType.createInstance();
				if (model.getObs() == null) {
					System.out.println("O campo 'obs' está nulo, inicializando com RichText vazio");
					model.setObs(new RichText());
				}

				// Inicializa o campo RichTextEditor para observações
				obsField = new RichTextEditor();
				obsField.setWidthFull();

				// Usa reflexão para acessar os métodos getter e setter do campo 'obs'
				Method getObsMethod = model.getClass().getMethod("getObs");
				Method setObsMethod = model.getClass().getMethod("setObs", RichText.class);

				// Configura o binding usando os métodos obtidos por reflexão
				binder.forField(obsField).withNullRepresentation("") // Representação nula para o campo de entrada
						.withConverter(new RichTextToMimeConverter()) // Aplicando o conversor
						.bind(model -> {
							try {
								return (RichText) getObsMethod.invoke(model);
							} catch (Exception e) {
								throw new RuntimeException("Erro ao acessar o campo 'obs' via reflexão", e);
							}
						}, (model, value) -> {
							try {
								// Invoca diretamente o setter com o objeto RichText
								setObsMethod.invoke(model, value);
							} catch (Exception e) {
								throw new RuntimeException("Erro ao definir o campo 'obs' via reflexão", e);
							}
						});

				// Layout para o campo de observações
				VerticalLayout obsFieldLayout = new VerticalLayout();
				obsFieldLayout.setWidthFull();
				obsFieldLayout.setPadding(false);

				// Rótulo para o campo de observações
				Span obsFieldLabel = new Span("Observações:");
				obsFieldLabel.getStyle().set("font-weight", "bold");
				obsFieldLabel.getStyle().set("margin-top", "10px");
				obsFieldLabel.getStyle().set("margin-bottom", "0px");

				// Adiciona o rótulo e o campo ao layout
				obsFieldLayout.add(obsFieldLabel, obsField);
				setColspan(obsFieldLayout, 2);

				// Adiciona o campo de observações ao binderFields para ser exibido em ordem
				binderFields.add(obsFieldLayout);
			}
		} catch (NoSuchFieldException | NoSuchMethodException e) {
			// O campo 'obs' não existe, não precisa fazer nada
			System.err.println("Campo 'obs' não encontrado: " + e.getMessage());
		} catch (Exception e) {
			System.err.println("Erro inesperado: " + e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * Aplica o estado de readOnly a todos os componentes do formulário.
	 */
	private void applyReadOnlyState(Component component, boolean isReadOnly) {
		if (component instanceof HasValue) {
			// Se o componente é um campo, aplica o estado de readOnly
			HasValue<?, ?> field = (HasValue<?, ?>) component;
			if (isReadOnly || readOnlyFields.contains(field)) {
				field.setReadOnly(true);
			} else {
				field.setReadOnly(false);
			}
		} else {
			// Itera sobre todos os filhos do componente
			component.getChildren().forEach(child -> applyReadOnlyState(child, isReadOnly));
		}
	}

	/**
	 * Atualiza o estado de readOnly para todos os componentes do formulário.
	 */
	public void updateReadOnlyState() {
		applyReadOnlyState(this, isReadOnly);
	}

	protected abstract void initBinder();

	/**
	 * Método abstrato para que as subclasses adicionem seus próprios componentes
	 * personalizados. Será chamado entre a adição dos campos do Binder e os botões.
	 */
	protected abstract void addCustomComponents();

	@Autowired
	public void setMailService(MailService mailService) {
		this.mailService = mailService;
	}

	protected T createModel() {
		try {
			return modelType.getDeclaredConstructor().newInstance();
		} catch (Exception e) {
			throw new RuntimeException("Nao foi possivel criar o modelo - " + modelType, e);
		}
	}

	public void initAnexos() {
		if (anexosCarregados) {
			if (!isReadOnly) {
				initUploadFiles();
			}
			add(verticalLayoutAnexos);
			return;
		}
		if (verticalLayoutAnexos == null) {
			verticalLayoutAnexos = new VerticalLayout();
			setColspan(verticalLayoutAnexos, 2);
		} else {
			verticalLayoutAnexos.removeAll();
		}
		if (!isReadOnly) {
			initUploadFiles();
		}
		if (model.getFileNames() != null && !model.getFileNames().isEmpty()) {
			// Percorre a lista de nomes de arquivos do modelo
			AbstractService.FileResponse fileResponse;
			for (String fileName : model.getFileNames()) {
				fileResponse = service.getAnexo(unid, fileName);

				if (fileResponse != null && fileResponse.isSuccess()) {
					byte[] fileData = fileResponse.getFileData();

					if (fileData != null && fileData.length > 0) {
						// Cria o StreamResource para download
						StreamResource streamResource = new StreamResource(fileName,
								() -> new ByteArrayInputStream(fileData));
						streamResource.setContentType(fileResponse.getMediaType());
						streamResource.setCacheTime(0);

						// Cria o Anchor para download
						Anchor downloadLink = new Anchor(streamResource,
								String.format("%s (%d KB)", fileName, fileData.length / 1024));
						downloadLink.getElement().setAttribute("download", true);

						// Adiciona o link de download ao layout
						verticalLayoutAnexos.add(downloadLink);
					} else {
						Notification.show("Erro ao obter o arquivo: " + fileName + " - Nenhum dado recebido.", 3000,
								Notification.Position.MIDDLE);
					}
				} else {
					Notification.show(
							"Erro ao buscar o anexo: " + fileName + " - "
									+ (fileResponse != null ? fileResponse.getMessage() : "Resposta nula"),
							3000, Notification.Position.MIDDLE);
				}

			}
		}

		// Adiciona o layout ao componente
		add(verticalLayoutAnexos);
		anexosCarregados = true;
	}

	public void initUploadFiles() {
		UploadI18N i18n = new UploadI18N();
		i18n.setAddFiles(new UploadI18N.AddFiles().setOne("Adicionar arquivo") // Texto do botão para um arquivo
				.setMany("Adicionar arquivos")); // Texto do botão para vários arquivos
		i18n.setDropFiles(new UploadI18N.DropFiles().setOne("Arraste o arquivo aqui") // Texto para arrastar um arquivo
				.setMany("Arraste os arquivos aqui")); // Texto para arrastar vários arquivos
		upload.setI18n(i18n);

		upload.setMaxFiles(10);
		upload.addSucceededListener(event -> {
			// Obter o nome do arquivo e o conteúdo
			String fileName = event.getFileName();
			InputStream fileData = buffer.getInputStream();

			// Exibir uma notificação com detalhes do arquivo
			Notification.show("Upload bem-sucedido: " + fileName);

			// Aqui você pode processar o arquivo conforme necessário (ex: salvar no
			// servidor)
			// Exemplo de leitura do conteúdo do arquivo
			processFile(fileName, fileData);
		});
		verticalLayoutAnexos.add(upload);

	}

	private void processFile(String fileName, InputStream fileData) {
		// Lógica para processar o arquivo (exemplo: salvar no sistema de arquivos ou
		// banco de dados)
		// Aqui, você pode implementar a lógica de armazenamento conforme a necessidade
	}

	public void initButtons() {
		saveButton = new Button("Salvar");
		cancelButton = new Button("Cancelar");
		deleteButton = new Button("Excluir");
		editButton = new Button("Editar");
		saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
		cancelButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
		deleteButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
		editButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
		// cancelButton.addClickShortcut(Key.ESCAPE);
		// saveButton.addClickShortcut(Key.ENTER);
		saveButton.addClickListener(e -> save());
		cancelButton.addClickListener(e -> cancel());
		deleteButton.addClickListener(e -> openConfirmDeleteDialog());
		editButton.addClickListener(e -> edit());
		showButtons();
	}

	protected void showButtons() {
		if (horizontalLayoutButtons == null) {
			horizontalLayoutButtons = new HorizontalLayout();
		} else {
			horizontalLayoutButtons.removeAll();
		}
		horizontalLayoutButtons.setMargin(true);
		if (isNovo) {
			horizontalLayoutButtons.add(saveButton, cancelButton);
		} else if (!isNovo && !isReadOnly) {
			horizontalLayoutButtons.add(saveButton, deleteButton, cancelButton);
		} else if (!isNovo && isReadOnly) {
			horizontalLayoutButtons.add(editButton, cancelButton);
		} else {
			Notification.show("Erro mostrando os botoes de ação.");
		}
		add(horizontalLayoutButtons);
		setColspan(horizontalLayoutButtons, 2);

	}

//	public void initFooter() {
//		if (model != null) {
//			footer = new HorizontalLayout();
//			footer.addClassName("abstract-view-doc-footer");
//			autorSpan = new Span("Autor: " + model.getAutor());
//			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
//			if (model.getCriacao() != null) {
//				criacaoSpan = new Span("Criação: " + model.getCriacao().format(formatter));
//			} else {
//				criacaoSpan = new Span("");
//			}
//
//			idSpan = new Span("Id: " + model.getId());
//			footer.add(autorSpan, criacaoSpan, idSpan);
//			add(footer, 3);
//		}
//	}

	public void initFooter() {
		if (footer == null) {
			footer = new HorizontalLayout();
			footer.setWidthFull();
			footer.addClassName("abstract-view-doc-footer");

			// Estilos para integrar o footer
			footer.getStyle().set("position", "sticky");
			footer.getStyle().set("bottom", "0");
			footer.getStyle().set("left", "0");
			footer.getStyle().set("right", "0");
			footer.getStyle().set("width", "100%"); // Garante que ocupe toda a largura
			footer.getStyle().set("background-color", "var(--lumo-base-color)");
			footer.getStyle().set("padding", "10px");
			footer.getStyle().set("box-shadow", "0 -1px 3px rgba(0, 0, 0, 0.1)");
			footer.getStyle().set("z-index", "10");

			autorSpan = new Span("Autor: " + (model.getAutor() != null ? model.getAutor() : "Desconhecido"));
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
			criacaoSpan = new Span(
					"Criação: " + (model.getCriacao() != null ? model.getCriacao().format(formatter) : ""));
			idSpan = new Span("Id: " + (model.getId() != null ? model.getId() : ""));

			footer.add(autorSpan, criacaoSpan, idSpan);
		}

		// Adiciona o footer diretamente ao layout da página (UI), fora do FormLayout
		if (!footer.getParent().isPresent()) {
			UI.getCurrent().getElement().appendChild(footer.getElement());
		}
	}

	protected T findByUnid(String unid) {
		AbstractService<T>.Response<T> response = service.findByUnid(unid);
		if (response.isSuccess()) {
			return response.getModel(); // Retorna o modelo do tipo `T`, não `AbstractModelDoc`
		} else {
			Notification.show("Erro ao buscar o documento: " + response.getMessage(), 5000,
					Notification.Position.MIDDLE);
			return createModel(); // Retorna um modelo vazio de `T` se der erro
		}
	}

	private void openConfirmDeleteDialog() {
		Dialog dialog = new Dialog();
		Span message = new Span("Tem certeza que deseja apagar " + model.getForm() + " - " + model.getCodigo() + "?");
		Button confirmButton = new Button("Confirmar", event -> {
			delete();
			dialog.close();
			UI.getCurrent().getPage().getHistory().back();
		});
		confirmButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
		Button cancelButton = new Button("Cancelar", event -> dialog.close());

		cancelButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
		HorizontalLayout cancelButtonsLayout = new HorizontalLayout(confirmButton, cancelButton);
		cancelButtonsLayout.getStyle().set("padding-top", "10px");

		dialog.add(message, cancelButtonsLayout);
		dialog.open();
	}

	// protected abstract SaveResponse save();

	public void save() {
		try {
			// SaveResponse saveResponse = null;
			binder.validate();
			SaveResponse saveResponse = service.save(model);
//			if (isNovo) {
//				saveResponse = service.save(model);
//			} else {
//				saveResponse = service.put(model);
//			}

			if (saveResponse != null) {
				if (saveResponse.getStatus() == null) {
					Notification notification = Notification.show("Documento Salvo com Sucesso!");
					notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
					// UI.getCurrent().getPage().reload(); // mesma pagina (salvar)
					UI.getCurrent().getPage().getHistory().back(); // (salvar e sair)
					return;
				} else if (saveResponse.getStatus().equals("403")) {
					Notification notification = Notification.show(saveResponse.getMessage());
					notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
					return;
				} else {
					Notification notification = Notification.show(saveResponse.getMessage());
					notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
					return;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * O readOnly só funciona se estiver depois do add(componente)
	 * 
	 */
	protected void readOnly() {
		isReadOnly = true;

	}

	/**
	 * Temos duas camadas de controle de erro, uma delas fica no delete do
	 * AbstractService e outro aqui. O motivo disto é que antes de retornar para
	 * esta função, temos que testar um erro de HTTP.
	 * 
	 * @return
	 */
	protected DeleteResponse delete() {
		DeleteResponse deleteResponse = null;
		try {
			deleteResponse = service.delete(model);
			if (deleteResponse != null) {
				Notification notification = Notification.show(deleteResponse.getMessage());

				String status = deleteResponse.getStatus(); // Certifique-se de que o status sempre será uma string
				if (status != null) {
					if (status.equals("403")) {
						notification.setText("Seu usuário não tem direitos para apagar este documento");
						notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
						return deleteResponse;
					}
					if (status.equals("500")) {
						notification.setText("Erro 500");
						notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
						return deleteResponse;
					}
					if (status.equals("501")) {
						notification.setText("Erro 501");
						notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
						return deleteResponse;
					}
					if (status.equals("200") || status.equals("OK")) {
						notification.setText("Documento apagado");
						notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
						return deleteResponse;
					}
				} else {
					notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
					notification.setText("Erro inesperado: status é nulo.");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return deleteResponse;
	}

	protected void edit() {
		isReadOnly = false;
		updateView();

	}

	protected void openPage(T model) {
		if (model != null && model.getMeta().getUnid() != null) {
			getUI().ifPresent(ui -> ui
					.navigate(model.getClass().getSimpleName().toLowerCase() + "/" + model.getMeta().getUnid()));
		} else {
			Notification.show("Erro: O documento não possui um ID válido para navegação.", 5000,
					Notification.Position.MIDDLE);
		}
	}

	protected void cancel() {
		UI.getCurrent().getPage().getHistory().back();
	}

	protected void sendMail() {
		mailService.sendSimpleMessage("mcastro@tdec.com.br", "Teste", "Conteudo de mensagem em texto simples.");

	}

	public static void print(Object obj) {
		System.out.println(obj.toString());
	}

}
