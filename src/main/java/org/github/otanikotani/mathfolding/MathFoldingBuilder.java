package org.github.otanikotani.mathfolding;


import com.intellij.lang.ASTNode;
import com.intellij.lang.folding.CustomFoldingBuilder;
import com.intellij.lang.folding.FoldingDescriptor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.FoldingGroup;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethodCallExpression;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class MathFoldingBuilder extends CustomFoldingBuilder {

    private final MathFoldingEngine mathFoldingEngine = new MathFoldingEngine();

    @Override
    protected void buildLanguageFoldRegions(
        @NotNull List<FoldingDescriptor> descriptors, @NotNull PsiElement root,
        @NotNull Document document, boolean quick) {
        FoldingGroup group = FoldingGroup.newGroup("Math folding");

        Collection<PsiMethodCallExpression> methodCallExpressions =
            PsiTreeUtil.findChildrenOfType(root, PsiMethodCallExpression.class);

        List<PsiMethodCallExpression> parentless = methodCallExpressions.stream()
            .filter(MathFoldingBuilder::hasNoMathParent)
            .collect(Collectors.toList());

        List<FoldingDescriptor> mathFoldingDescriptors = parentless.stream()
            .filter(expression -> expression.getMethodExpression().getText().startsWith("Math."))
            .map(expression -> new FoldingDescriptor(expression.getNode(), expression.getTextRange(), group,
                mathFoldingEngine.fold(expression.getText())))
            .collect(Collectors.toList());
        descriptors.addAll(mathFoldingDescriptors);
    }

    @Override
    protected String getLanguagePlaceholderText(@NotNull ASTNode node, @NotNull TextRange range) {
        return "";
    }

    private static boolean hasNoMathParent(PsiMethodCallExpression pmce) {
        PsiElement mathParent = PsiTreeUtil.findFirstParent(pmce, true, psiElement -> {
            if (psiElement instanceof PsiMethodCallExpression) {
                PsiMethodCallExpression parent = (PsiMethodCallExpression) psiElement;
                return parent.getMethodExpression().getText().contains("Math.");
            }
            return false;
        });
        return mathParent == null;
    }

    @Override
    protected boolean isRegionCollapsedByDefault(@NotNull ASTNode node) {
        return false;
    }
}
