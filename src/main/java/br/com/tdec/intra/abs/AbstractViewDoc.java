package br.com.tdec.intra.abs;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
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
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.UploadI18N;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.OptionalParameter;
import com.vaadin.flow.router.QueryParameters;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.theme.lumo.LumoUtility.Display;
import com.vaadin.flow.theme.lumo.LumoUtility.Flex;
import com.vaadin.flow.theme.lumo.LumoUtility.Margin;
import com.vaadin.flow.theme.lumo.LumoUtility.Width;

import br.com.tdec.intra.abs.AbstractService.DeleteResponse;
import br.com.tdec.intra.abs.AbstractService.SaveResponse;
import br.com.tdec.intra.config.MailService;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
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
	protected Upload upload = new Upload(buffer);
	protected HorizontalLayout footer;
	protected Span autorSpan;
	protected Span criacaoSpan;
	protected Span idSpan;

	public AbstractViewDoc(Class<T> modelType, AbstractService<T> service) {
		this.service = service;
		this.modelType = modelType;
		binder = new Binder<>(modelType);

		addClassNames("abstract-view-doc.css", Width.FULL, Display.FLEX, Flex.AUTO, Margin.LARGE);

		this.setTitle(title);
		setWidth("100%");
		getStyle().set("flex-grow", "0");

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

	}

	/**
	 * sempre que algum componente foi alterado, tenho que refazer toda a tela.
	 * Apenas adicinoar um componente em caso de edit, por exemplo, ira colocá-lo
	 * fora de ordem. Primeiro o Binder, depois os richtext, depois o upload, depois
	 * os files, depois os botoes e depois o foooter.
	 */
	public void updateView() {
		initBinder(); // roda no modelo
		binder.setBean(model); // Limpa o layout e adiciona os campos na ordem correta
		this.removeAll();
		binderFields.forEach(this::add);

		if (model.getFileNames() != null && !model.getFileNames().isEmpty()) {
			initAnexos();
		}
		initButtons();
		initFooter();
		// tem que vir depois de todos os campos serem adicionados no FormLayout
		if (isReadOnly) {
			// Itera sobre todos os componentes filhos da classe
			super.getChildren().forEach(component -> {
				if (component instanceof HasValue) {
					HasValue<?, ?> field = (HasValue<?, ?>) component;
					field.setReadOnly(true);
				}
			});
		} else {
			Iterator<Component> i = super.getChildren().iterator();
			while (i.hasNext()) {
				Component c = i.next();
				if (c instanceof HasValue) {
					HasValue<?, ?> field = (HasValue<?, ?>) c;
					if (!readOnlyFields.contains(field)) {
						field.setReadOnly(false);
					}
				}
			}
		}

	}

	protected abstract void initBinder();

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
		System.out.println("Iniciando exibição de anexos");

		if (verticalLayoutAnexos == null) {
			verticalLayoutAnexos = new VerticalLayout();
		} else {
			verticalLayoutAnexos.removeAll();
		}
		if (!isReadOnly) {
			initUploadFiles();
		}
		// Percorre a lista de nomes de arquivos do modelo
		for (String fileName : model.getFileNames()) {
			String unid = model.getMeta().getUnid();

			System.out.println("Preparando botão para o arquivo: " + fileName);

			// Cria o botão de download
			Button downloadButton = new Button("Download " + fileName, VaadinIcon.DOWNLOAD.create());
			downloadButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

			// Listener para buscar o anexo quando o botão é clicado
			downloadButton.addClickListener(clickEvent -> {
				System.out.println("Usuário clicou para baixar o arquivo: " + fileName);

				// Chama o serviço para obter o anexo
				AbstractService.FileResponse anexoResponse = service.getAnexo(unid, fileName);

				if (anexoResponse != null && anexoResponse.isSuccess()) {
					byte[] fileData = anexoResponse.getFileData();

					if (fileData != null && fileData.length > 0) {
						// Cria o StreamResource para permitir o download
						StreamResource resource = new StreamResource(fileName,
								() -> new ByteArrayInputStream(fileData));
						resource.setContentType(anexoResponse.getMediaType());
						resource.setCacheTime(0);

						// Cria o Anchor para download
						Anchor downloadLink = new Anchor(resource, "Clique aqui para baixar " + fileName);
						downloadLink.getElement().setAttribute("download", true);

						// Adiciona o link de download ao layout
						UI.getCurrent().access(() -> {
							Notification.show("Arquivo pronto para download: " + fileName, 3000,
									Notification.Position.MIDDLE);
							verticalLayoutAnexos.add(downloadLink);
						});
					} else {
						System.out.println("Nenhum dado recebido para o arquivo: " + fileName);
						Notification.show("Erro ao obter o arquivo: " + fileName + " - Nenhum dado recebido.", 3000,
								Notification.Position.MIDDLE);
					}
				} else {
					System.out.println("Erro ao buscar o anexo: " + fileName + " - "
							+ (anexoResponse != null ? anexoResponse.getMessage() : "Resposta nula"));
					Notification.show(
							"Erro ao buscar o anexo: " + fileName + " - "
									+ (anexoResponse != null ? anexoResponse.getMessage() : "Resposta nula"),
							3000, Notification.Position.MIDDLE);
				}
			});

			// Adiciona o botão de download ao layout
			verticalLayoutAnexos.add(downloadButton);
		}

		// Adiciona o layout ao componente
		add(verticalLayoutAnexos);
		// setColspan(verticalLayoutAnexos, 2);
	}

//	public void initAnexosTest() {
//		System.out.println("Iniciando teste de anexo para 'fernando.html'");
//
//		// Nome do arquivo que queremos testar
//		String fileName = "fernando.html";
//		String unid = model.getMeta().getUnid();
//
//		try {
//			System.out.println("Chamando getAnexo() para o documento: " + unid + " e arquivo: " + fileName);
//
//			// Chama o serviço para obter o anexo
//			AbstractService.FileResponse anexoResponse = service.getAnexo(unid, fileName);
//
//			// Verifica o resultado da chamada
//			if (anexoResponse != null) {
//				System.out.println("Resposta recebida:");
//				System.out.println("Status Code: " + anexoResponse.getStatusCode());
//				System.out.println("Mensagem: " + anexoResponse.getMessage());
//				System.out.println("Media Type: " + anexoResponse.getMediaType());
//				System.out.println("File Name: " + anexoResponse.getFileName());
//
//				// Verifica se os dados do arquivo foram recebidos
//				byte[] fileData = anexoResponse.getFileData();
//				if (fileData != null && fileData.length > 0) {
//					System.out
//							.println("Dados do arquivo recebidos com sucesso. Tamanho: " + fileData.length + " bytes");
//				} else {
//					System.out.println("Nenhum dado recebido para o arquivo.");
//				}
//			} else {
//				System.out.println("Resposta nula recebida do serviço.");
//			}
//		} catch (Exception e) {
//			System.out.println("Erro ao buscar anexo: " + e.getMessage());
//			e.printStackTrace();
//		}
//	}

	public void initUploadFiles() {
		UploadI18N i18n = new UploadI18N();
		i18n.setAddFiles(new UploadI18N.AddFiles().setOne("Adicionar arquivo") // Texto do botão para um arquivo
				.setMany("Adicionar arquivos")); // Texto do botão para vários arquivos
		i18n.setDropFiles(new UploadI18N.DropFiles().setOne("Arraste o arquivo aqui") // Texto para arrastar um arquivo
				.setMany("Arraste os arquivos aqui")); // Texto para arrastar vários arquivos
		upload.setI18n(i18n);

		upload.setMaxFiles(3);
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

	public void initFooter() {
		if (model != null) {
			footer = new HorizontalLayout();
			footer.addClassName("abstract-view-doc-footer");
			autorSpan = new Span("Autor: " + model.getAutor());
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
			if (model.getCriacao() != null) {
				criacaoSpan = new Span("Criação: " + model.getCriacao().format(formatter));
			} else {
				criacaoSpan = new Span("");
			}

			idSpan = new Span("Id: " + model.getId());
			footer.add(autorSpan, criacaoSpan, idSpan);
			add(footer, 3);
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
