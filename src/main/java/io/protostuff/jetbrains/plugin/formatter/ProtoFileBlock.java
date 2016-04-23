package io.protostuff.jetbrains.plugin.formatter;

import com.intellij.formatting.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.formatter.FormatterUtil;
import com.intellij.psi.formatter.common.AbstractBlock;
import com.intellij.psi.tree.IElementType;
import io.protostuff.compiler.parser.ProtoParser;
import io.protostuff.jetbrains.plugin.ProtoParserDefinition;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static io.protostuff.jetbrains.plugin.formatter.BlockFactory.createBlock;

/**
 * @author Kostiantyn Shchepanovskyi
 */
class ProtoFileBlock extends AbstractBlock {

    ProtoFileBlock(@NotNull ASTNode node, @Nullable Wrap wrap, @Nullable Alignment alignment) {
        super(node, wrap, alignment);

    }

    @Override
    protected List<Block> buildChildren() {
        List<Block> blocks = new ArrayList<>();
        ASTNode child = myNode.getFirstChildNode();
        while (child != null) {
            if (!FormatterUtil.containsWhiteSpacesOnly(child)) {
                IElementType elementType = child.getElementType();
                if (ProtoParserDefinition.rule(ProtoParser.RULE_proto).equals(elementType)) {
                    appendProtoBlocks(child, blocks);
                } else {
                    // Comments are not part of root rule, we have to append them separately
                    blocks.add(new LeafBlock(child, Alignment.createAlignment(), Indent.getNoneIndent()));
                }
            }
            child = child.getTreeNext();
        }
        return blocks;
    }

    private void appendProtoBlocks(ASTNode protoRootNode, List<Block> blocks) {
        ASTNode child = protoRootNode.getFirstChildNode();
        Alignment alignment = Alignment.createAlignment();
        while (child != null) {
            if (!FormatterUtil.containsWhiteSpacesOnly(child)) {
                Block block = createBlock(child, alignment);
                blocks.add(block);
            }
            child = child.getTreeNext();
        }
    }

    @Nullable
    @Override
    public Spacing getSpacing(@Nullable Block child1, @NotNull Block child2) {
        if (child1 == null) {
            return StatementBlock.NONE;
        }
        return StatementBlock.NEW_LINE;
    }

    @Override
    public boolean isLeaf() {
        return false;
    }

    @Override
    public Indent getIndent() {
        return Indent.getAbsoluteNoneIndent();
    }
}