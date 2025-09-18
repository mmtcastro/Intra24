package br.com.tdec.intra.abs;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serial;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.richtexteditor.RichTextEditor;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.UploadI18N;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.BeforeLeaveEvent;
import com.vaadin.flow.router.BeforeLeaveObserver;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.OptionalParameter;
import com.vaadin.flow.router.QueryParameters;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.server.streams.UploadHandler;
import com.vaadin.flow.server.streams.UploadMetadata;

import br.com.tdec.intra.abs.AbstractModelDoc.RichText;
import br.com.tdec.intra.abs.AbstractModelDoc.UploadedFile;
import br.com.tdec.intra.abs.AbstractService.DeleteResponse;
import br.com.tdec.intra.abs.AbstractService.FileResponse;
import br.com.tdec.intra.abs.AbstractService.SaveResponse;
import br.com.tdec.intra.components.MultivalueGrid;
import br.com.tdec.intra.config.ApplicationContextProvider;
import br.com.tdec.intra.config.MailService;
import br.com.tdec.intra.services.Response;
import br.com.tdec.intra.utils.converters.RichTextToMimeConverter;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class AbstractViewDoc<T extends AbstractModelDoc> extends Composite<VerticalLayout>
		implements HasUrlParameter<String>, BeforeLeaveObserver {

	@Serial
	private static final long serialVersionUID = 1L;
	protected VerticalLayout layout;
	protected FormLayout form;
	protected T model;
	protected Class<T> modelType;
	protected AbstractService<T> service;
	protected MailService mailService;
	@Autowired
	private ObjectMapper objectMapper; // jackson conversao zonedDateTime
	protected String unid;
	protected Binder<T> binder;
	protected RichTextEditor obsField = new RichTextEditor(); // opcional na classe concreta
	protected VerticalLayout obsFieldLayout; // para o colspan,2 e o titulo funcionarem
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
	// protected MemoryBuffer buffer = new MemoryBuffer();

	// protected Upload upload = new Upload(buffer);
	protected Map<String, byte[]> arquivos = new HashMap<>(); // Para armazenar os uploads em memória
	protected Upload upload;

	protected boolean showUploads = true; // alguns forms nao querem ter uploads
	protected boolean showObs = true; // alguns forms nao querem ter observações
	protected HorizontalLayout footer;
	protected Span autorSpan;
	protected Span criacaoSpan;
	protected Span idSpan;

	@SuppressWarnings("unchecked")
	public AbstractViewDoc() {
		layout = getContent();
		layout.setWidthFull();
		layout.setPadding(true);
		layout.setSpacing(true);

		form = new FormLayout();
		form.setExpandColumns(true); // Expande as colunas para ocupar o espaço disponível
		form.setExpandFields(true); // Expande os campos para ocupar o espaço disponível
		form.setWidthFull();
		form.setAutoResponsive(true);
		form.setResponsiveSteps(//
				new FormLayout.ResponsiveStep("0", 1), //
				new FormLayout.ResponsiveStep("600px", 2), //
				new FormLayout.ResponsiveStep("900px", 3));
		layout.add(form);

		this.modelType = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];

		// Buscar dinamicamente o Service correspondente ao modelo
		this.service = findService();

		binder = new Binder<>(modelType);

		this.setTitle(title);
		getStyle().set("flex-grow", "0");
		getStyle().set("margin-left", "10px");
		getStyle().set("margin-right", "10px");
		this.getStyle().set("padding-bottom", "60px"); // Ajuste a altura conforme necessário - footer

		// Ao clicar duas no form, entra em modo de edição

		layout.addDoubleClickListener(event -> edit());

		// Listener para desmontagem do componente
		addDetachListener(this::removeFooter);

	}

	protected abstract void initBinder();

	protected void showUploads() {
		showUploads = true;
	}

	@SuppressWarnings("unchecked")
	private AbstractService<T> findService() {
		// Obter o nome do modelo
		String modelName = modelType.getSimpleName();

		// Construir o nome do serviço a partir do nome do modelo
		String serviceName = modelName + "Service";

		// Converter para o formato camelCase (primeira letra minúscula)
		serviceName = Character.toLowerCase(serviceName.charAt(0)) + serviceName.substring(1);

		// Use the ApplicationContextProvider to get the ApplicationContext
		ApplicationContext context = ApplicationContextProvider.getApplicationContext();
		if (context == null) {
			throw new IllegalStateException("ApplicationContext is not initialized.");
		}

		Object serviceBean = context.getBean(serviceName);

		if (serviceBean instanceof AbstractService) {
			return (AbstractService<T>) serviceBean;
		} else {
			throw new IllegalStateException("Serviço não encontrado ou não é do tipo AbstractService: " + serviceName);
		}
	}

//	/**
//	 * Método abstrato para que as subclasses adicionem seus próprios componentes
//	 * personalizados. Será chamado entre a adição dos campos do Binder e os botões.
//	 */
//	protected abstract void addCustomComponents();

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
		// initFooter();

	}

	public void updateView() {
		// Inicializa o Binder
		initBinder();

		// Define o modelo
		binder.setBean(model);
		// Inicializa o campo de observações, se necessário
		if (showObs) {
			initObsFieldIfNeeded();

		}

		// Adiciona os anexos (upload e arquivos)
		if (showUploads) {
			initAnexos();
		}

		// Adiciona os botões de ação
		initButtons();

		// Atualiza o estado de readOnly
		updateReadOnlyState();

	}

	/**
	 * Inicializa o campo de observações (RichTextEditor) se o modelo tiver o campo
	 * 'obs'.
	 */

	private void initObsFieldIfNeeded() {
		// Verifica se o layout já foi inicializado
		if (obsFieldLayout != null && binderFields.contains(obsFieldLayout)) {
			System.out.println("Campo de observações já inicializado, não será criado novamente.");
			return;
		}

		// Inicializa o layout para o campo 'obs'
		obsFieldLayout = new VerticalLayout();
		// obsFieldLayout.setWidthFull();
		obsFieldLayout.setPadding(false);

		// Verifica se o modelo contém o campo 'obs'
		boolean hasObsField = false;
		for (Field field : model.getClass().getDeclaredFields()) {
			if (field.getName().equals("obs")) {
				hasObsField = true;
				break;
			}
		}

		if (!hasObsField) {
			// Caso o campo 'obs' não exista, apenas registre como uma decisão de design
			System.out.println("O modelo não contém o campo 'obs'. Nenhum campo de observações será inicializado.");
			return; // Não adiciona obsFieldLayout
		}

		// Configurações para o campo de observações (RichTextEditor)
		try {
			Field obsFieldModel = model.getClass().getDeclaredField("obs");
			obsFieldModel.setAccessible(true);

			// Inicializa o campo 'obs' no modelo, se estiver nulo
			if (obsFieldModel.get(model) == null) {
				obsFieldModel.set(model, new RichText());
			}

			obsField = new RichTextEditor();

			// Configura o binding
			binder.forField(obsField).withNullRepresentation("").withConverter(new RichTextToMimeConverter())
					.bind(model -> {
						try {
							return (RichText) obsFieldModel.get(model);
						} catch (Exception e) {
							throw new RuntimeException("Erro ao acessar o campo 'obs' via reflexão", e);
						}
					}, (model, value) -> {
						try {
							obsFieldModel.set(model, value);
						} catch (Exception e) {
							throw new RuntimeException("Erro ao definir o campo 'obs' via reflexão", e);
						}
					});

			// Adiciona o rótulo e o campo ao layout
			Span obsFieldLabel = new Span("Observações");
			obsFieldLabel.getElement().getStyle().set("font-size", "var(--lumo-font-size-s)");
			obsFieldLabel.getElement().getStyle().set("color", "var(--lumo-header-text-color)"); // Cor padrão para
																									// títulos
			obsFieldLabel.getStyle().set("margin-bottom", "5px"); // Pequena margem abaixo do título
			obsFieldLabel.getStyle().set("margin-top", "10px"); // Pequena margem abaixo do título

			obsFieldLayout.add(obsFieldLabel, obsField);

			if (obsFieldLayout != null) {
				form.addFormRow(obsFieldLayout);
				form.setColspan(obsFieldLayout, 2); // Colspan para ocupar duas colunas
			}
			// this.setColspan(obsFieldLayout, 2);

		} catch (Exception e) {
			System.err.println("Erro inesperado ao inicializar o campo de observações: " + e.getMessage());
		}
	}

	/**
	 * Aplica o estado de readOnly a todos os componentes do formulário.
	 */
	private void applyReadOnlyState(Component component, boolean isReadOnly) {
		if (component instanceof HasValue<?, ?> field) {
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
		// Verificar o estado do obsField
		if (obsField != null) {
			String value = obsField.getValue();
			boolean isObsEmpty = (value == null || value.trim().isEmpty() || value.equals("<p><br></p>"));

			// Esconder o campo obsField se estiver vazio e o documento for readOnly
			if (obsFieldLayout != null) {
				obsFieldLayout.setVisible(!(isReadOnly && isObsEmpty));
			}
		}
	}

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

		// Inicializa o layout de anexos
		if (verticalLayoutAnexos == null) {
			verticalLayoutAnexos = new VerticalLayout();
			// verticalLayoutAnexos.setWidthFull();
			verticalLayoutAnexos.setPadding(true);
			verticalLayoutAnexos.setSpacing(true);

			// Estiliza o layout para parecer uma "caixa"
			verticalLayoutAnexos.getStyle().set("border", "1px solid var(--lumo-contrast-10pct)");
			verticalLayoutAnexos.getStyle().set("border-radius", "8px");
			verticalLayoutAnexos.getStyle().set("padding", "15px");
			verticalLayoutAnexos.getStyle().set("background-color", "var(--lumo-base-color)");
			verticalLayoutAnexos.getStyle().set("box-shadow", "0 2px 4px rgba(0, 0, 0, 0.1)");
			verticalLayoutAnexos.getStyle().set("margin-top", "20px"); // Adiciona margem superior
		} else {
			verticalLayoutAnexos.removeAll();
		}

		// Título da seção de anexos
		Icon folderIcon = VaadinIcon.FOLDER.create();
		folderIcon.getStyle().set("color", "var(--lumo-primary-color)");
		folderIcon.getStyle().set("margin-right", "5px"); // Espaçamento entre o ícone e o texto
		folderIcon.getStyle().set("width", "14px"); // Largura proporcional ao texto 12px
		folderIcon.getStyle().set("height", "14px"); // Altura proporcional ao texto 12px

		Span anexosLabel = new Span("Anexos");
		anexosLabel.getStyle().set("font-weight", "bold");
		anexosLabel.getStyle().set("font-size", "12px");
		anexosLabel.getStyle().set("line-height", "14px"); // Alinha o texto com o ícone, se necessário

		// Layout horizontal para o título
		HorizontalLayout titleLayout = new HorizontalLayout(folderIcon, anexosLabel);
		titleLayout.setDefaultVerticalComponentAlignment(Alignment.CENTER); // Alinha verticalmente
		titleLayout.setSpacing(false); // Remove espaços adicionais
		titleLayout.getStyle().set("margin-bottom", "10px"); // Espaço abaixo do título

		verticalLayoutAnexos.add(titleLayout);

		// Lista de arquivos anexados
		VerticalLayout fileListLayout = new VerticalLayout();
		fileListLayout.setPadding(false);
		fileListLayout.setSpacing(false);
		// fileListLayout.setWidthFull();

//		// Adiciona os arquivos anexados à lista
		if (model.getFileNames() != null && !model.getFileNames().isEmpty()) {
			// System.out.println("AbstactViewDow InitAnexos - Arquivos anexados: " +
			// model.getFileNames());
			for (String fileName : model.getFileNames()) {
				AbstractService.FileResponse fileResponse = service.getAnexo(unid, fileName);

				if (fileResponse != null) {
					byte[] fileData = fileResponse.getFileData();
					StreamResource streamResource = new StreamResource(fileName,
							() -> new ByteArrayInputStream(fileData));
					streamResource.setContentType(fileResponse.getMediaType());

					Anchor fileLink = new Anchor(streamResource, fileName + " (" + fileData.length / 1024 + " KB)");
					fileLink.getElement().setAttribute("download", true);
					fileLink.getStyle().set("padding", "5px 0");

					fileListLayout.add(fileLink);

					// Layout horizontal para alinhar o link e o botão
					HorizontalLayout fileRow = new HorizontalLayout();
					// fileRow.setWidthFull();
					fileRow.setAlignItems(Alignment.CENTER);

					// Adicionar o botão "Excluir" apenas se não estiver em modo read-only
					if (!isReadOnly) {
						Button deleteButton = new Button("", VaadinIcon.TRASH.create());
						deleteButton.addClickListener(event -> deleteAnexo(fileName));
						deleteButton.addThemeName("error");
						fileRow.add(deleteButton);
					}

					fileRow.add(fileLink);

					fileListLayout.add(fileRow);

//					// Adicionar o anexo ao modelo (se ainda não estiver lá)
//					if (model.getUploads().stream().noneMatch(a -> a.getFileName().equals(fileName))) {
//						model.adicionarAnexo(new UploadedFile(fileName, fileData));
//						model.getLogger().info("Anexo adicionado ao modelo: " + fileName);
//					}
				}
			}
		} else {
			Span noFilesLabel = new Span("Nenhum arquivo anexado.");
			noFilesLabel.getStyle().set("color", "var(--lumo-secondary-text-color)");
			fileListLayout.add(noFilesLabel);
		}

		verticalLayoutAnexos.add(fileListLayout);

		// Adiciona o componente de upload somente se não estiver em modo readOnly
		if (!isReadOnly) {
			initUploadFiles();
			verticalLayoutAnexos.add(upload);
		}

		// Adiciona o layout de anexos ao binderFields para controle de readOnly
		// binderFields.add(verticalLayoutAnexos);
		// anexosCarregados = true;

		// Adiciona o layout de anexos ao formulário
		form.addFormRow(verticalLayoutAnexos);
		form.setColspan(verticalLayoutAnexos, 2); // Colspan para ocupar duas colunas

	}

//	public void initUploadFiles() {
//		model.getLogger().info("Upload de arquivos - iniciando");
//
//		// Callback de sucesso: arquivo salvo temporariamente no disco
//		BiConsumer<UploadMetadata, File> successHandler = (metadata, file) -> {
//			String fileName = metadata.fileName();
//			model.getLogger().info("Arquivo recebido: " + fileName + " (" + file.length() + " bytes)");
//
//			try (InputStream inputStream = new FileInputStream(file)) {
//				byte[] fileBytes = inputStream.readAllBytes();
//
//				// Adiciona ao modelo
//				model.adicionarAnexo(new UploadedFile(fileName, fileBytes));
//
//			} catch (IOException e) {
//				model.getLogger().warn("Erro ao processar o arquivo: " + e.getMessage(), e);
//				Notification.show("Erro ao processar o arquivo: " + e.getMessage(), 3000, Notification.Position.MIDDLE);
//			}
//		};
//
//		// Usa tipo concreto diretamente
//		TemporaryFileUploadHandler handler = TemporaryFileUploadHandler.toTempFile(successHandler::accept);
//
//		handler.onProgress((transferredBytes, totalBytes) -> {
//			double percent = 100.0 * transferredBytes / totalBytes;
//			System.out.printf("Progresso do upload: %.1f%%%n", percent);
//		});
//
//		handler.onError((error, metadata) -> {
//			String fileName = metadata.fileName();
//			String errorMessage = "Falha ao enviar o arquivo " + fileName;
//
//			if (error instanceof MaxUploadSizeExceededException) {
//				errorMessage = "O arquivo " + fileName + " excede o tamanho máximo permitido!";
//			}
//
//			model.getLogger().warn(errorMessage, error);
//			Notification.show(errorMessage, 5000, Notification.Position.MIDDLE);
//		});
//
//		handler.whenComplete(success -> {
//			if (success) {
//				System.out.println("Upload concluído com sucesso.");
//			} else {
//				System.out.println("Falha no upload.");
//			}
//		});
//
//		// Cria o componente de upload
//		upload = new Upload(handler);
//		upload.setMaxFiles(10);
//		upload.setMaxFileSize(50 * 1024 * 1024); // 50 MB
//		upload.setAcceptedFileTypes("application/pdf");
//
//		// Traduções
//		UploadI18N i18n = new UploadI18N()
//				.setAddFiles(new UploadI18N.AddFiles().setOne("Adicionar arquivo").setMany("Adicionar arquivos"))
//				.setDropFiles(new UploadI18N.DropFiles().setOne("Arraste o arquivo aqui")
//						.setMany("Arraste os arquivos aqui"));
//		upload.setI18n(i18n);
//
//		// ✅ Clique no ❌ do Vaadin Upload
//		upload.getElement().addEventListener("file-remove", e -> {
//			String fileName = e.getEventData().getString("event.detail.file.name");
//			deleteAnexo(fileName);
//			model.getLogger().info("Arquivo removido pelo usuário (X do Upload): " + fileName);
//		}).addEventData("event.detail.file.name");
//	}

	public void initUploadFiles() {
		model.getLogger().info("Upload de arquivos - iniciando");

		// Callback de sucesso: arquivo já está salvo temporariamente no disco
		BiConsumer<UploadMetadata, File> successHandler = (metadata, file) -> {
			String fileName = metadata.fileName();
			model.getLogger().info("Arquivo recebido: " + fileName + " (" + file.length() + " bytes)");

			try (InputStream inputStream = new FileInputStream(file)) {
				byte[] fileBytes = inputStream.readAllBytes();

				// Adiciona ao modelo
				model.adicionarAnexo(new UploadedFile(fileName, fileBytes));

			} catch (IOException e) {
				model.getLogger().warn("Erro ao processar o arquivo: " + e.getMessage());
				Notification.show("Erro ao processar o arquivo: " + e.getMessage(), 3000, Notification.Position.MIDDLE);
			}
		};

		// Cria handler com progresso
		UploadHandler handler = UploadHandler.toTempFile(successHandler::accept)
				.onProgress((transferredBytes, totalBytes) -> {
					double percent = 100.0 * transferredBytes / totalBytes;
					System.out.printf("Progresso do upload: %.1f%%%n", percent);
				}).whenComplete(success -> {
					if (success) {
						System.out.println("Upload concluído com sucesso.");
					} else {
						System.out.println("Falha no upload.");
					}
				});

		// Cria componente de upload
		upload = new Upload(handler);
		upload.setMaxFiles(10);
		upload.setMaxFileSize(50 * 1024 * 1024); // 50 MB
		upload.setAcceptedFileTypes("application/pdf");

		// Traduções
		UploadI18N i18n = new UploadI18N()
				.setAddFiles(new UploadI18N.AddFiles().setOne("Adicionar arquivo").setMany("Adicionar arquivos"))
				.setDropFiles(new UploadI18N.DropFiles().setOne("Arraste o arquivo aqui")
						.setMany("Arraste os arquivos aqui"));
		upload.setI18n(i18n);

		// Falhas
		upload.addFailedListener(event -> {
			String errorMessage = "Falha ao enviar o arquivo.";
			if (event.getReason() instanceof MaxUploadSizeExceededException) {
				errorMessage = "O arquivo excede o tamanho máximo permitido!";
			}
			Notification.show(errorMessage, 5000, Notification.Position.MIDDLE);
		});

		// ✅ Clique no ❌ do Vaadin Upload
		upload.getElement().addEventListener("file-remove", e -> {
			String fileName = e.getEventData().getString("event.detail.file.name");
			deleteAnexo(fileName);
			model.getLogger().info("Arquivo removido pelo usuário (X do Upload): " + fileName);
		}).addEventData("event.detail.file.name");
	}

	private void deleteAnexo(String fileName) {
		// Marca para exclusão no modelo
		model.getAnexosParaExcluir().add(fileName);

		// Remove de uploads (anexos novos, em memória)
		boolean removedFromUploads = model.getUploads().removeIf(f -> f.getFileName().equalsIgnoreCase(fileName));

		// Remove de fileNames (anexos já persistidos no Domino)
		boolean removedFromFileNames = model.getFileNames().removeIf(fn -> fn.trim().equalsIgnoreCase(fileName.trim()));

		// Feedback ao usuário
		if (removedFromUploads || removedFromFileNames) {
//			Notification.show("O anexo \"" + fileName + "\" foi marcado para exclusão.", 3000,
//					Notification.Position.MIDDLE);
		} else {
			Notification.show("Não foi possível localizar o anexo \"" + fileName + "\" na lista.", 3000,
					Notification.Position.MIDDLE);
		}

		model.getLogger().info("Uploads restantes: " + model.getUploads().size());
		model.getLogger().info("Anexos para excluir: " + model.getAnexosParaExcluir().size());

		// Atualiza só a lista de anexos visível (sem recriar toda a view)
		refreshAnexosUI();
	}

	private void refreshAnexosUI() {
		if (verticalLayoutAnexos != null) {
			verticalLayoutAnexos.removeAll();
			initAnexos(); // seu método que recria botões/links de anexos
		}
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
		layout.add(horizontalLayoutButtons);
		// layout.setColspan(horizontalLayoutButtons, 2);

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
			// footer.setWidthFull();
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
		if (footer.getParent().isEmpty()) {
			UI.getCurrent().getElement().appendChild(footer.getElement());
		}
	}

	protected T findByUnid(String unid) {
		Response<T> response = service.findByUnid(unid);
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
			// Remove o footer antes de sair
			if (footer != null && footer.getParent().isPresent()) {
				footer.getElement().removeFromParent();
			}
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
			if (!binder.isValid()) {
				Notification notification = Notification.show("Erro de validação");
				notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
				return;
			}

			// Remove o footer antes de sair
			if (footer != null && footer.getParent().isPresent()) {
				footer.getElement().removeFromParent();
			}

			// 🔑 SINCRONIZA TODOS OS MultivalueGrids AQUI (caso existam no model)
			getChildren().filter(c -> c instanceof MultivalueGrid<?>)//
					.map(c -> (MultivalueGrid<?>) c) //
					.forEach(MultivalueGrid::syncAndSaveIfEditing);

			SaveResponse saveResponse = service.save(model);

			if (saveResponse != null) {
				if (saveResponse.getStatus() == null) {
					// primeiro vamos apagar um anexo marcado para exclusão
					for (String fileName : model.getAnexosParaExcluir()) {
						FileResponse response = service.deleteAnexo(saveResponse.getMeta().getUnid(), fileName);
						if (response != null && response.isSuccess()) {
							Notification.show("Anexo apagado: " + fileName, 2000, Notification.Position.MIDDLE);
						} else {
							Notification.show("Falha ao apagar anexo: " + fileName, 4000, Notification.Position.MIDDLE);
						}
					}
					FileResponse anexosResponse = service.getAttachmentNames(saveResponse.getMeta().getUnid());
					List<String> naoGravarNovamente = anexosResponse.getFileNames();

					for (UploadedFile file : model.getUploads()) {
						if (!naoGravarNovamente.contains(file.getFileName())) {
							FileResponse response = service.uploadAnexo(saveResponse.getMeta().getUnid(), "anexos",
									file.getFileName(), new ByteArrayInputStream(file.getFileData()));

							if (response != null && response.isSuccess()) {
								Notification.show("Anexo enviado: " + file.getFileName(), 2000,
										Notification.Position.MIDDLE);
							} else {
								Notification.show("Falha ao enviar anexo: " + file.getFileName(), 4000,
										Notification.Position.MIDDLE);
							}
						}
					}

					model.getUploads().clear(); // Limpa após enviar
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
			} else {
				Notification notification = Notification.show("Verificar direito de gravacao no REST API");
				notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
				return;
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
						// Usar a mensagem retornada pelo DeleteResponse ao invés de uma fixa
						String errorMessage = deleteResponse.getMessage() != null ? deleteResponse.getMessage()
								: "Seu usuário não tem direitos para apagar este documento";
						notification.setText(errorMessage);
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
						notification.setText(model.getForm() + " apagado");
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
		if (!isReadOnly) {
			return;
		}
		isReadOnly = false;
		setReadOnlyRecursive(layout, false); // Aplica em tudo
		initAnexos();
		showButtons();
		updateReadOnlyState(); // ← esta linha é essencial

	}

	private void setReadOnlyRecursive(Component component, boolean readOnly) {
		if (component instanceof HasValue<?, ?> hasValue) {
			hasValue.setReadOnly(readOnly);
		}

		// Trata Grids com editores
		if (component instanceof Grid<?> grid) {
			Binder<?> binder = grid.getEditor().getBinder();
			if (binder != null) {
				binder.getFields().forEach(field -> {
					if (field instanceof HasValue<?, ?> campo) {
						campo.setReadOnly(readOnly);
					}
				});
			}
		}

		component.getChildren().forEach(child -> setReadOnlyRecursive(child, readOnly));
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
//		// Remove o footer antes de voltar
//		if (footer != null && footer.getParent().isPresent()) {
//			footer.getElement().removeFromParent();
//		}
		UI.getCurrent().getPage().getHistory().back();
	}

	protected void sendMail() {
		mailService.sendSimpleMessage("mcastro@tdec.com.br", "Teste", "Conteudo de mensagem em texto simples.");

	}

	public static void print(Object obj) {
		System.out.println(obj.toString());
	}

	@Override
	public void beforeLeave(BeforeLeaveEvent event) {
		removeFooter();
	}

	private void removeFooter(DetachEvent event) {
		removeFooter();
	}

	private void removeFooter() {
		if (footer != null && footer.getParent().isPresent()) {
			footer.getElement().removeFromParent();
		}
	}

	/**
	 * Adiciona um componente ao binderFields e define seu tamanho baseado em `1` ou
	 * `2`.
	 * 
	 * - `1` → O componente ocupa as duas colunas (Full Width) - `2` → O componente
	 * ocupa apenas metade da largura
	 * 
	 * @param component O componente a ser adicionado
	 * @param widthMode 1 para Full Width (duas posições), 2 para metade (uma
	 *                  posição)
	 */
	protected void addComponentToBinderFields(Component component, int widthMode) {
		if (component == null) {
			throw new IllegalArgumentException("O componente não pode ser nulo");
		}

		binderFields.add(component);

		switch (widthMode) {
		case 1:
			// setColspan(component, 2); // Ocupa toda a largura (2 colunas)
			component.getElement().getStyle().set("width", "100%");
			break;
		case 2:
			// setColspan(component, 1); // Ocupa apenas 1 coluna (meia largura)
			break;
		default:
			throw new IllegalArgumentException("O widthMode deve ser 1 (Full Width) ou 2 (Meia Largura)");
		}
	}

}
