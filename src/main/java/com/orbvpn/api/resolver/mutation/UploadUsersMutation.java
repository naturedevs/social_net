package com.orbvpn.api.resolver.mutation;

import com.orbvpn.api.service.UploadUserService;
import graphql.kickstart.servlet.context.DefaultGraphQLServletContext;
import graphql.kickstart.tools.GraphQLMutationResolver;
import graphql.schema.DataFetchingEnvironment;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import javax.servlet.http.Part;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UploadUsersMutation implements GraphQLMutationResolver {
  private final UploadUserService uploadUserService;

  public boolean uploadUsers(DataFetchingEnvironment dataFetchingEnvironment) {
    DefaultGraphQLServletContext context = dataFetchingEnvironment.getContext();

    List<Part> fileParts = context.getFileParts();

    if(fileParts.size() != 1 ) {
      throw new RuntimeException("You can upload users from one file");
    }

    Part part = fileParts.get(0);
    try {
      InputStream inputStream = part.getInputStream();
      uploadUserService.uploadUsers(inputStream);
    } catch (IOException e) {
      throw new RuntimeException("Can not access file stream");
    }

    return true;
  }
}
