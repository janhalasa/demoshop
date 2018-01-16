package com.halasa.demoshop.app.doc;

import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.PayloadDocumentation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class FieldDescriptorListBuilder {

    private List<FieldDescriptor> fieldDescriptors = new ArrayList<>();
    private String prefix = null;

    public static FieldDescriptorListBuilder of(FieldDescriptor... descriptors) {
        return new FieldDescriptorListBuilder().add(descriptors);
    }

    public static FieldDescriptorListBuilder from(FieldDescriptorListBuilder builder) {
        return new FieldDescriptorListBuilder().addAll(builder.build());
    }

    public FieldDescriptorListBuilder withPrefix(String prefix) {
        this.prefix = prefix;
        return this;
    }

    public FieldDescriptorListBuilder add(FieldDescriptor... descriptors) {
        fieldDescriptors.addAll(Arrays.asList(descriptors));
        return this;
    }

    public FieldDescriptorListBuilder addAll(List<FieldDescriptor> descriptorList) {
        fieldDescriptors.addAll(descriptorList);
        return this;
    }

    public List<FieldDescriptor> build() {
        if (this.prefix != null) {
            return fieldDescriptors.stream()
                    .map(fieldDescriptor -> {
                        final FieldDescriptor descriptorWithPrefix = PayloadDocumentation.fieldWithPath(prefix + fieldDescriptor.getPath());
                        if (fieldDescriptor.getDescription() != null) {
                            descriptorWithPrefix.description(fieldDescriptor.getDescription());
                        }
                        if (fieldDescriptor.isOptional()) {
                            descriptorWithPrefix.optional();
                        }
                        if (fieldDescriptor.getType() != null) {
                            descriptorWithPrefix.type(fieldDescriptor.getType());
                        }
                        if (fieldDescriptor.isIgnored()) {
                            descriptorWithPrefix.ignored();
                        }
                        return descriptorWithPrefix;
                    })
                    .collect(Collectors.toList());
        }
        return new ArrayList<>(this.fieldDescriptors);
    }
}
