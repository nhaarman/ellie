package ollie.internal.codegen.writer;

import com.google.common.collect.Sets;
import com.squareup.javawriter.JavaWriter;
import ollie.internal.AdapterHolder;
import ollie.internal.ModelAdapter;
import ollie.internal.codegen.Registry;
import ollie.internal.codegen.element.MigrationElement;
import ollie.internal.codegen.element.ModelAdapterElement;
import ollie.internal.codegen.element.TypeAdapterElement;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import java.io.IOException;
import java.io.Writer;
import java.util.*;

import static javax.lang.model.element.Modifier.*;

public class AdapterHolderWriter implements SourceWriter<TypeElement> {
	private static final Set<Modifier> METHOD_MODIFIERS = EnumSet.of(PUBLIC, FINAL);
	private static final Set<Modifier> CONSTANT_MODIFIERS = EnumSet.of(PRIVATE, STATIC, FINAL);

	private Registry registry;

	public AdapterHolderWriter(Registry registry) {
		this.registry = registry;
	}

	@Override
	public String createSourceName(TypeElement element) {
		return AdapterHolder.IMPL_CLASS_FQCN;
	}

	@Override
	public void writeSource(Writer writer, TypeElement element) throws IOException {
		JavaWriter javaWriter = new JavaWriter(writer);
		javaWriter.setCompressingTypes(true);
		javaWriter.setIndent("\t");
		javaWriter.emitSingleLineComment("Generated by Ollie. Do not modify!");

		javaWriter.emitPackage("ollie");

		writeImports(javaWriter);

		javaWriter.beginType(AdapterHolder.IMPL_CLASS_NAME, "class", METHOD_MODIFIERS, null, "AdapterHolder");

		writeCollections(javaWriter);
		writeStaticInitializations(javaWriter);
		writeGetMigrations(javaWriter);
		writeGetModelAdapter(javaWriter);
		writeGetModelAdapters(javaWriter);
		writeGetTypeAdpater(javaWriter);

		javaWriter.endType();
	}

	private void writeImports(JavaWriter writer) throws IOException {
		Set<String> imports = Sets.newHashSet(
				ArrayList.class.getName(),
				HashMap.class.getName(),
				List.class.getName(),
				Map.class.getName(),
				AdapterHolder.class.getName(),
				ModelAdapter.class.getName()
		);

		Set<TypeAdapterElement> typeAdapters = registry.getTypeAdapters();
		for (TypeAdapterElement typeAdapter : typeAdapters) {
			imports.add(typeAdapter.getQualifiedName());
		}

		Set<MigrationElement> migrationElements = registry.getMigrationElements();
		for (MigrationElement migrationElement : migrationElements) {
			imports.add(migrationElement.getQualifiedName());
		}

		writer.emitImports(imports);
		writer.emitEmptyLine();
	}

	private void writeCollections(JavaWriter writer) throws IOException {
		writer.emitField(
				"List<Migration>",
				"MIGRATIONS",
				CONSTANT_MODIFIERS,
				"new ArrayList<Migration>()"
		);
		writer.emitField(
				"Map<Class<? extends Model>, ModelAdapter>",
				"MODEL_ADAPTERS",
				CONSTANT_MODIFIERS,
				"new HashMap<Class<? extends Model>, ModelAdapter>()"
		);
		writer.emitField(
				"Map<Class, TypeAdapter>",
				"TYPE_ADAPTERS",
				CONSTANT_MODIFIERS,
				"new HashMap<Class, TypeAdapter>()"
		);
		writer.emitEmptyLine();
	}

	private void writeStaticInitializations(JavaWriter writer) throws IOException {
		writer.beginInitializer(true);

		for (MigrationElement migration : registry.getMigrationElements()) {
			writer.emitStatement("MIGRATIONS.add(new %s())",
					migration.getSimpleName());
		}
		writer.emitEmptyLine();

		for (ModelAdapterElement modelAdapter : registry.getModelAdapterElements()) {
			writer.emitStatement("MODEL_ADAPTERS.put(%s.class, new %s())",
					modelAdapter.getModelQualifiedName(),
					modelAdapter.getQualifiedName());
		}
		writer.emitEmptyLine();

		for (TypeAdapterElement typeAdapter : registry.getTypeAdapters()) {
			writer.emitStatement("TYPE_ADAPTERS.put(%s.class, new %s())",
					typeAdapter.getDeserializedQualifiedName(),
					typeAdapter.getSimpleName());
		}

		writer.endInitializer();
		writer.emitEmptyLine();
	}

	private void writeGetMigrations(JavaWriter writer) throws IOException {
		writer.beginMethod("List<? extends Migration>", "getMigrations", METHOD_MODIFIERS);
		writer.emitStatement("return MIGRATIONS");
		writer.endMethod();
		writer.emitEmptyLine();
	}

	private void writeGetModelAdapter(JavaWriter writer) throws IOException {
		writer.beginMethod("<T extends Model> ModelAdapter<T>", "getModelAdapter", METHOD_MODIFIERS,
				"Class<? extends Model>", "cls");
		writer.emitStatement("return MODEL_ADAPTERS.get(cls)");
		writer.endMethod();
		writer.emitEmptyLine();
	}

	private void writeGetModelAdapters(JavaWriter writer) throws IOException {
		writer.beginMethod("List<? extends ModelAdapter>", "getModelAdapters", METHOD_MODIFIERS);
		writer.emitStatement("return new ArrayList(MODEL_ADAPTERS.values())");
		writer.endMethod();
		writer.emitEmptyLine();
	}

	private void writeGetTypeAdpater(JavaWriter writer) throws IOException {
		writer.beginMethod("<D, S> TypeAdapter<D, S>", "getTypeAdapter", METHOD_MODIFIERS, "Class<D>", "cls");
		writer.emitStatement("return TYPE_ADAPTERS.get(cls)");
		writer.endMethod();
	}
}
