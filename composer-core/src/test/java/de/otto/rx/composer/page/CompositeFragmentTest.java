package de.otto.rx.composer.page;

import com.google.common.collect.ImmutableList;
import de.otto.rx.composer.content.Content;
import de.otto.rx.composer.content.Headers;
import de.otto.rx.composer.content.Position;
import de.otto.rx.composer.content.SingleContent;
import de.otto.rx.composer.providers.ContentProvider;
import de.otto.rx.composer.tracer.Tracer;
import org.junit.Test;

import static com.google.common.collect.ImmutableList.copyOf;
import static de.otto.rx.composer.content.AbcPosition.A;
import static de.otto.rx.composer.content.AbcPosition.B;
import static de.otto.rx.composer.content.Parameters.emptyParameters;
import static de.otto.rx.composer.page.Fragments.followedBy;
import static de.otto.rx.composer.tracer.NoOpTracer.noOpTracer;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static rx.Observable.just;

public class CompositeFragmentTest {

    @Test
    public void shouldExecuteInitialContentProvider() {
        // given
        final Tracer tracer = noOpTracer();
        final ContentProvider initial = mock(ContentProvider.class);
        when(initial.getContent(A, tracer, emptyParameters())).thenReturn(just(mock(Content.class)));
        final Fragment fragment = Fragments.fragment(A, initial, followedBy((c) -> emptyParameters(), mock(Fragment.class)));
        // when
        fragment.fetchWith(tracer, emptyParameters());
        // then
        verify(initial).getContent(A, tracer, emptyParameters());
    }

    @Test
    public void shouldExecuteNestedFragment() {
        // given
        final Tracer tracer = noOpTracer();
        final ContentProvider fetchInitial = mock(ContentProvider.class);
        when(fetchInitial.getContent(A, tracer, emptyParameters())).thenReturn(just(someContent(A)));
        // and
        final Fragment nestedFragment = mock(Fragment.class);
        when(nestedFragment.getPosition()).thenReturn(B);
        when(nestedFragment.fetchWith(tracer, emptyParameters())).thenReturn(just(someContent(B)));
        // and
        final Fragment compositeFragment = Fragments.fragment(A, fetchInitial, followedBy((c) -> emptyParameters(), nestedFragment));
        // when
        ImmutableList<Content> contents = copyOf(compositeFragment.fetchWith(tracer, emptyParameters()).toBlocking().toIterable());

        // then
        assertThat(contents, hasSize(2));
        verify(nestedFragment).fetchWith(tracer, emptyParameters());
    }

    private Content someContent(final Position position) {
        return new SingleContent() {
            @Override
            public String getSource() {
                return position.name();
            }

            @Override
            public Position getPosition() {
                return position;
            }

            @Override
            public boolean isAvailable() {
                return true;
            }

            @Override
            public String getBody() {
                return "Yes!";
            }

            @Override
            public Headers getHeaders() {
                return Headers.emptyHeaders();
            }

            @Override
            public long getStartedTs() {
                return 0L;
            }

            @Override
            public long getCompletedTs() {
                return 0L;
            }

        };

    }

}