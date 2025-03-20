package com.example.simplenotas.ui.notes

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.simplenotas.data.model.Note
import com.example.simplenotas.ui.components.ClickableTextContent
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.ui.graphics.Color

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesScreen(
    viewModel: NotesViewModel,
    onNoteClick: (Note) -> Unit,
    onCreateNote: () -> Unit,
    onShareNote: (Note) -> Unit,
    onLegalScreenOpen: (String, String) -> Unit
) {
    val notes by viewModel.notes.collectAsState(initial = emptyList())
    val searchQuery by viewModel.searchQuery.collectAsState()
    val previewSize by viewModel.previewSize.collectAsState()
    var isSearchVisible by remember { mutableStateOf(false) }
    var showMenu by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Simple Notas") },
                actions = {
                    IconButton(onClick = { isSearchVisible = !isSearchVisible }) {
                        Icon(Icons.Default.Search, contentDescription = "Search")
                    }
                    IconButton(onClick = { showMenu = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "More options")
                    }
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false },
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        DropdownMenuItem(
                            text = { Text("Configurações") },
                            onClick = {
                                showMenu = false
                                // Adicionar ação de configurações aqui
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Política de Privacidade") },
                            onClick = {
                                showMenu = false
                                val privacyContent = "Política de Privacidade - Simple Notas\n\nÚltima atualização: [data]\n\nO Simple Notas valoriza a privacidade de seus usuários e está comprometido em proteger as informações coletadas. Esta Política de Privacidade esclarece como os dados são armazenados, utilizados e protegidos ao utilizar o aplicativo.\n\n1. Coleta de Informações\nO Simple Notas não coleta informações pessoais identificáveis. No entanto, para garantir o funcionamento adequado do aplicativo, algumas informações podem ser armazenadas, tais como:\n\n- Notas e Conteúdos: Todas as anotações criadas são armazenadas localmente no dispositivo do usuário. Caso o usuário opte por sincronizar os dados na nuvem, será utilizado o serviço Firebase para backup seguro.\n- Dados Anônimos: O aplicativo pode coletar estatísticas anônimas, como a quantidade de notas criadas, para fins de melhoria da experiência do usuário.\n\n2. Uso das Informações\nOs dados armazenados são utilizados exclusivamente para as seguintes finalidades:\n\n- Permitir que o usuário acesse e gerencie suas anotações.\n- Sincronizar notas na nuvem, caso a opção de backup esteja ativada.\n- Melhorar o funcionamento do aplicativo com base em análises estatísticas.\n\n3. Compartilhamento de Informações\nO Simple Notas não compartilha dados pessoais dos usuários com terceiros. No entanto, caso o usuário opte por sincronizar os dados na nuvem, as informações serão armazenadas conforme as políticas de privacidade do Google Drive e Firebase.\n\n4. Segurança dos Dados\nMedidas de segurança são implementadas para proteger os dados armazenados no aplicativo, incluindo criptografia e mecanismos de autenticação quando disponíveis. No entanto, o usuário é responsável por garantir a proteção do dispositivo e das informações armazenadas.\n\n5. Permissões do Aplicativo\nPara o funcionamento adequado, o Simple Notas pode solicitar determinadas permissões, como:\n\n- Armazenamento: Necessário para salvar e carregar notas no dispositivo.\n- Acesso à Internet: Necessário apenas para a sincronização opcional com a nuvem.\n\n6. Publicidade e Monetização\nO Simple Notas pode exibir anúncios como forma de financiamento para manter o aplicativo gratuito. Além disso, poderá ser oferecida uma versão premium sem anúncios e com funcionalidades adicionais.\n\n7. Alterações na Política de Privacidade\nEsta Política de Privacidade pode ser atualizada periodicamente. O usuário será notificado em caso de alterações significativas.\n\n8. Contato\nPara dúvidas ou solicitações relacionadas à Política de Privacidade, entre em contato pelo e-mail [seu e-mail de suporte]."
                                onLegalScreenOpen("Política de Privacidade", privacyContent)
                            },
                            leadingIcon = null,
                            trailingIcon = null
                        )
                        DropdownMenuItem(
                            text = { Text("Termos de Uso") },
                            onClick = {
                                showMenu = false
                                val termsContent = "Termos de Uso - Simple Notas\n\nÚltima atualização: [data]\n\nO Simple Notas é um aplicativo desenvolvido para facilitar a criação e organização de notas em dispositivos Android. Ao utilizar este serviço, o usuário concorda com os seguintes Termos de Uso.\n\n1. Aceitação dos Termos\nO uso do Simple Notas está sujeito a estes Termos de Uso. Caso o usuário não concorde com as condições estabelecidas, recomenda-se a descontinuação do uso do aplicativo.\n\n2. Uso do Aplicativo\nO Simple Notas permite a criação, edição e organização de notas de forma pessoal e intransferível. O usuário deve utilizar o aplicativo de maneira responsável e em conformidade com as leis aplicáveis.\n\n3. Responsabilidades do Usuário\nO usuário é responsável pelo conteúdo armazenado no aplicativo. O Simple Notas não deve ser utilizado para armazenar informações sensíveis, como senhas, dados bancários ou informações confidenciais.\n\n4. Limitações de Responsabilidade\nO Simple Notas é disponibilizado \"como está\", sem qualquer garantia de funcionamento ininterrupto ou isento de falhas. Não nos responsabilizamos por perdas de dados decorrentes do uso inadequado do aplicativo, falhas técnicas ou remoção acidental de informações pelo usuário.\n\n5. Backup e Sincronização\nO aplicativo oferece a possibilidade de sincronização das notas na nuvem, sendo esta uma funcionalidade opcional. Caso o usuário opte por realizar backups, os dados estarão sujeitos às políticas do Google Drive e Firebase.\n\n6. Publicidade e Versão Premium\nO Simple Notas pode exibir anúncios publicitários para viabilizar sua operação. Também poderá ser disponibilizada uma versão premium, isenta de anúncios e com funcionalidades adicionais.\n\n7. Alterações nos Termos de Uso\nOs Termos de Uso poderão ser modificados a qualquer momento, sem aviso prévio. O uso contínuo do aplicativo após alterações implica a aceitação dos novos termos.\n\n8. Contato\nPara suporte ou esclarecimentos, entre em contato pelo e-mail [seu e-mail de suporte]."
                                onLegalScreenOpen("Termos de Uso", termsContent)
                            },
                            leadingIcon = null,
                            trailingIcon = null
                        )
                        DropdownMenuItem(
                            text = { Text("Sobre") },
                            onClick = {
                                showMenu = false
                                // Adicionar ação sobre aqui
                            },
                            leadingIcon = null,
                            trailingIcon = null
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onCreateNote) {
                Icon(Icons.Default.Add, contentDescription = "Add Note")
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            if (isSearchVisible || searchQuery.isNotEmpty()) {
                SearchBar(
                    query = searchQuery,
                    onQueryChange = viewModel::setSearchQuery,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(notes) { note ->
                    NoteCard(
                        note = note,
                        previewSize = previewSize,
                        onClick = { onNoteClick(note) },
                        onDelete = { viewModel.deleteNote(note) },
                        onShare = { onShareNote(note) },
                        onDuplicate = { viewModel.duplicateNote(note) },
                        onIncreasePreview = { viewModel.increasePreviewSize() },
                        onDecreasePreview = { viewModel.decreasePreviewSize() }
                    )
                }
            }
        }
    }
}

@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier,
        placeholder = { Text("Search notes...") },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
        singleLine = true
    )
}

@Composable
fun NoteCard(
    note: Note,
    previewSize: Int,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    onShare: () -> Unit,
    onDuplicate: () -> Unit,
    onIncreasePreview: () -> Unit,
    onDecreasePreview: () -> Unit
) {
    val dateFormat = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }
    
    // Definir a cor de fundo com base no backgroundColor da nota
    val backgroundColor = note.backgroundColor?.let { Color(it.toInt()) } ?: MaterialTheme.colorScheme.surface

    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = note.title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                Row {
                    IconButton(onClick = onShare) {
                        Icon(Icons.Default.Share, contentDescription = "Share note")
                    }
                    IconButton(onClick = onDuplicate) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = "Duplicate note")
                    }
                    IconButton(onClick = onDelete) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete note")
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            
            // Usando o componente ClickableTextContent para exibir o conteúdo com links e telefones clicáveis
            Box(modifier = Modifier.height(previewSize.dp)) {
                ClickableTextContent(
                    text = note.content,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            
            // Controles para aumentar/diminuir o tamanho da prévia
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(
                    onClick = onDecreasePreview,
                    modifier = Modifier.size(24.dp)
                ) {
                    Text("-", style = MaterialTheme.typography.labelLarge)
                }
                IconButton(
                    onClick = onIncreasePreview,
                    modifier = Modifier.size(24.dp)
                ) {
                    Text("+", style = MaterialTheme.typography.labelLarge)
                }
            }
            
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Modified: ${dateFormat.format(note.modifiedAt)}",
                style = MaterialTheme.typography.bodySmall
            )
            if (note.category.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = note.category,
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    }
}