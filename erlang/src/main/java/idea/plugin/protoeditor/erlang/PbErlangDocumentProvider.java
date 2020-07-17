package idea.plugin.protoeditor.erlang;

import com.google.common.collect.ImmutableCollection;
import com.intellij.lang.documentation.AbstractDocumentationProvider;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import idea.plugin.protoeditor.lang.psi.*;
import idea.plugin.protoeditor.lang.psi.util.PbCommentUtil;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class PbErlangDocumentProvider extends AbstractDocumentationProvider {
  public PbErlangDocumentProvider() {
    super();
  }

  @Nullable
  @Override
  public String generateDoc(PsiElement element, @Nullable PsiElement originalElement) {
    ErlFileReferenceContext context = ErlFileReferenceContext.findContext(originalElement);
    if (context != null && originalElement!=null) {
      ImmutableCollection<? extends PbElement> matches = PbErlangGotoDeclarationHandler.erlangToProto(context, originalElement.getProject());
      if (matches == null || matches.isEmpty()) {
        return null;
      }
      PbElement pbElement = matches.iterator().next();
      return generatePbDoc(pbElement);
    }
    return null;
  }

  public String generatePbDoc(PbElement element) {
    if (!(element instanceof PbCommentOwner)) {
      return null;
    }

    PbCommentOwner owner = (PbCommentOwner) element;
    List<PsiComment> comments = owner.getComments();
    if (comments.isEmpty()) {
      return null;
    }

    StringBuilder commentBuilder = new StringBuilder("<pre>");
    for (String line : PbCommentUtil.extractText(comments)) {
      commentBuilder.append(StringUtil.escapeXmlEntities(line));
      commentBuilder.append("\n");
    }
    appendPbMessage(commentBuilder, element);
    commentBuilder.append("</pre>");
    return commentBuilder.toString();
  }

  private void appendPbMessage(StringBuilder commentBuilder, PbElement pbElement){
    if (pbElement instanceof PbSimpleField)
      commentBuilder.append(pbElement.getText()).append("\n");
    PbMessageDefinition definition = PsiTreeUtil.getParentOfType(pbElement, PbMessageDefinition.class, false, PbFile.class);
    if (definition != null){
      commentBuilder.append("\n").append(definition.getText());
    }
  }

}
